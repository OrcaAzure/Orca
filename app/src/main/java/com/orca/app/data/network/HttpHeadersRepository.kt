package com.orca.app.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
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
    val headers: List<HttpHeaderEntry>,
)

@Singleton
class HttpHeadersRepository @Inject constructor() {

    suspend fun fetchHeaders(urlInput: String): Result<HttpHeadersResult> = withContext(Dispatchers.IO) {
        val trimmed = urlInput.trim()
        if (trimmed.isBlank()) return@withContext Result.failure(IllegalArgumentException("Enter a URL"))

        val urlString = normalizeUrl(trimmed)
        var connection: HttpURLConnection? = null

        try {
            val start = System.currentTimeMillis()
            connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "HEAD"
                connectTimeout = 15_000
                readTimeout = 15_000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "Orca/1.1.0")
            }

            connection.connect()
            val elapsed = System.currentTimeMillis() - start
            val statusCode = connection.responseCode
            val statusMessage = connection.responseMessage ?: "Unknown"

            val headers = connection.headerFields
                .filterKeys { it != null }
                .flatMap { (name, values) ->
                    values.map { value -> HttpHeaderEntry(name!!, value) }
                }
                .sortedBy { it.name.lowercase() }

            Result.success(
                HttpHeadersResult(
                    url = urlString,
                    statusCode = statusCode,
                    statusMessage = statusMessage,
                    responseTimeMs = elapsed,
                    headers = headers,
                ),
            )
        } catch (e: Exception) {
            Result.failure(Exception("Request failed: ${e.message ?: "Unknown error"}"))
        } finally {
            connection?.disconnect()
        }
    }

    private fun normalizeUrl(input: String): String {
        return when {
            input.startsWith("http://") || input.startsWith("https://") -> input
            else -> "https://$input"
        }
    }
}
