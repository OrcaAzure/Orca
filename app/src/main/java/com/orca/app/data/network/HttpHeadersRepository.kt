package com.orca.app.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class HttpHeaderEntry(
    val name: String,
    val value: String,
)

data class HttpHeadersResult(
    val url: String,
    val statusCode: Int,
    val statusMessage: String,
    val responseTimeMs: Long,
    val requestMethod: String,
    val redirectChain: List<String>,
    val headers: List<HttpHeaderEntry>,
)

@Singleton
class HttpHeadersRepository @Inject constructor() {

    suspend fun fetchHeaders(urlInput: String): Result<HttpHeadersResult> = withContext(Dispatchers.IO) {
        val trimmed = urlInput.trim()
        if (trimmed.isBlank()) return@withContext Result.failure(IllegalArgumentException("Enter a URL"))

        val urlString = normalizeUrl(trimmed)

        try {
            val start = System.currentTimeMillis()

            var fetchResult = fetchWithRedirects(urlString, "HEAD")
            if (fetchResult.statusCode == HttpURLConnection.HTTP_BAD_METHOD ||
                fetchResult.statusCode == HttpURLConnection.HTTP_NOT_IMPLEMENTED
            ) {
                fetchResult = fetchWithRedirects(urlString, "GET")
            }

            val elapsed = System.currentTimeMillis() - start

            Result.success(
                HttpHeadersResult(
                    url = fetchResult.finalUrl,
                    statusCode = fetchResult.statusCode,
                    statusMessage = fetchResult.statusMessage,
                    responseTimeMs = elapsed,
                    requestMethod = fetchResult.method,
                    redirectChain = fetchResult.redirectChain,
                    headers = fetchResult.headers,
                ),
            )
        } catch (e: Exception) {
            Result.failure(Exception("Request failed: ${e.message ?: "Unknown error"}"))
        }
    }

    private data class FetchResult(
        val finalUrl: String,
        val statusCode: Int,
        val statusMessage: String,
        val method: String,
        val redirectChain: List<String>,
        val headers: List<HttpHeaderEntry>,
    )

    private fun fetchWithRedirects(urlString: String, method: String): FetchResult {
        val redirects = mutableListOf<String>()
        var currentUrl = urlString

        repeat(MAX_REDIRECTS) {
            val connection = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = 15_000
                readTimeout = 15_000
                instanceFollowRedirects = false
                setRequestProperty("User-Agent", "Orca/1.3.0")
            }

            try {
                connection.connect()
                val statusCode = connection.responseCode
                val statusMessage = connection.responseMessage ?: "Unknown"

                if (statusCode in 300..399) {
                    val location = connection.getHeaderField("Location")
                    if (location.isNullOrBlank()) {
                        return buildFetchResult(currentUrl, statusCode, statusMessage, method, redirects, connection)
                    }
                    val resolved = resolveRedirect(currentUrl, location)
                    redirects.add("$statusCode $statusMessage → $resolved")
                    currentUrl = resolved
                    return@repeat
                }

                if (method == "GET") {
                    runCatching { connection.inputStream.use { stream -> stream.readBytes() } }
                }

                return buildFetchResult(currentUrl, statusCode, statusMessage, method, redirects, connection)
            } finally {
                connection.disconnect()
            }
        }

        throw Exception("Too many redirects (max $MAX_REDIRECTS)")
    }

    private fun buildFetchResult(
        url: String,
        statusCode: Int,
        statusMessage: String,
        method: String,
        redirectChain: List<String>,
        connection: HttpURLConnection,
    ): FetchResult {
        val headers = connection.headerFields
            .filterKeys { it != null }
            .flatMap { (name, values) ->
                values.map { value -> HttpHeaderEntry(name!!, value) }
            }
            .sortedBy { it.name.lowercase() }

        return FetchResult(url, statusCode, statusMessage, method, redirectChain, headers)
    }

    private fun resolveRedirect(baseUrl: String, location: String): String {
        return if (location.startsWith("http://") || location.startsWith("https://")) {
            location
        } else {
            URI(baseUrl).resolve(location).toString()
        }
    }

    private fun normalizeUrl(input: String): String {
        return when {
            input.startsWith("http://") || input.startsWith("https://") -> input
            else -> "https://$input"
        }
    }

    companion object {
        private const val MAX_REDIRECTS = 10
    }
}
