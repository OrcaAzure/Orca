package com.orca.app.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

data class WhoisResult(
    val query: String,
    val server: String,
    val response: String,
)

@Singleton
class WhoisRepository @Inject constructor() {

    private val tldServers = mapOf(
        "com" to "whois.verisign-grs.com",
        "net" to "whois.verisign-grs.com",
        "org" to "whois.pir.org",
        "io" to "whois.nic.io",
        "co" to "whois.nic.co",
        "dev" to "whois.nic.google",
        "app" to "whois.nic.google",
        "edu" to "whois.educause.edu",
        "gov" to "whois.dotgov.gov",
        "uk" to "whois.nic.uk",
        "de" to "whois.denic.de",
        "fr" to "whois.nic.fr",
        "info" to "whois.afilias.net",
        "biz" to "whois.biz",
        "me" to "whois.nic.me",
        "xyz" to "whois.nic.xyz",
    )

    suspend fun lookup(query: String): Result<WhoisResult> = withContext(Dispatchers.IO) {
        val domain = extractDomain(query)
        if (domain.isBlank()) return@withContext Result.failure(IllegalArgumentException("Enter a domain name"))

        // CTF-only TLDs have no public WHOIS
        val tld = domain.substringAfterLast('.', "").lowercase()
        if (tld in listOf("htb", "thm", "local", "internal", "corp")) {
            return@withContext Result.success(
                WhoisResult(
                    query = domain,
                    server = "n/a",
                    response = "No public WHOIS available for .$tld domains.\n" +
                        "This is a private/CTF-only TLD."
                )
            )
        }

        try {
            val primaryServer = tldServers[tld] ?: "whois.iana.org"
            var response = queryWhoisServer(primaryServer, domain)

            // Follow IANA referral if needed
            if (primaryServer == "whois.iana.org") {
                val referMatch = Regex("refer:\\s*(\\S+)", RegexOption.IGNORE_CASE).find(response)
                val referServer = referMatch?.groupValues?.getOrNull(1)
                if (referServer != null) {
                    runCatching { response = queryWhoisServer(referServer, domain) }
                    return@withContext Result.success(
                        WhoisResult(query = domain, server = referServer, response = response.trim())
                    )
                }
            }

            Result.success(WhoisResult(query = domain, server = primaryServer, response = response.trim()))
        } catch (e: Exception) {
            Result.failure(Exception("WHOIS lookup failed: ${e.message ?: "Unknown error"}"))
        }
    }

    private fun extractDomain(input: String): String {
        return input.trim()
            .removePrefix("http://")
            .removePrefix("https://")
            .removePrefix("www.")
            .substringBefore('/')
            .substringBefore(':')
    }

    private fun queryWhoisServer(server: String, query: String): String {
        Socket(server, 43).use { socket ->
            socket.soTimeout = 15_000
            PrintWriter(socket.getOutputStream(), true).println(query)
            return BufferedReader(InputStreamReader(socket.getInputStream())).readText()
        }
    }
}
