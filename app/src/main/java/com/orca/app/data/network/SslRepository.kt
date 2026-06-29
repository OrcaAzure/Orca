package com.orca.app.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

data class SslCertificateResult(
    val host: String,
    val port: Int,
    val subject: String,
    val issuer: String,
    val validFrom: String,
    val validTo: String,
    val serialNumber: String,
    val signatureAlgorithm: String,
    val version: Int,
    val subjectAltNames: List<String>,
    val isExpired: Boolean,
    val daysUntilExpiry: Long,
)

@Singleton
class SslRepository @Inject constructor() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    suspend fun inspect(host: String, port: Int = 443): Result<SslCertificateResult> = withContext(Dispatchers.IO) {
        val trimmed = host.trim().removePrefix("https://").removePrefix("http://").substringBefore('/')
        if (trimmed.isBlank()) return@withContext Result.failure(IllegalArgumentException("Enter a hostname"))

        try {
            val trustAll = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                },
            )

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAll, java.security.SecureRandom())

            val socket = sslContext.socketFactory.createSocket(trimmed, port) as SSLSocket
            socket.soTimeout = 15_000
            socket.startHandshake()

            val certs = socket.session.peerCertificates
            socket.close()

            if (certs.isEmpty()) return@withContext Result.failure(Exception("No certificate found"))

            val cert = certs[0] as X509Certificate
            val now = Date()
            val notAfter = cert.notAfter
            val daysUntil = (notAfter.time - now.time) / (1000 * 60 * 60 * 24)

            val sans = cert.subjectAlternativeNames?.mapNotNull { entry ->
                entry.getOrNull(1)?.toString()
            } ?: emptyList()

            Result.success(
                SslCertificateResult(
                    host = trimmed,
                    port = port,
                    subject = cert.subjectX500Principal.name,
                    issuer = cert.issuerX500Principal.name,
                    validFrom = dateFormat.format(cert.notBefore),
                    validTo = dateFormat.format(notAfter),
                    serialNumber = cert.serialNumber.toString(16).uppercase(),
                    signatureAlgorithm = cert.sigAlgName,
                    version = cert.version,
                    subjectAltNames = sans,
                    isExpired = notAfter.before(now),
                    daysUntilExpiry = daysUntil,
                ),
            )
        } catch (e: Exception) {
            Result.failure(Exception("SSL inspection failed: ${e.message ?: "Unknown error"}"))
        }
    }
}
