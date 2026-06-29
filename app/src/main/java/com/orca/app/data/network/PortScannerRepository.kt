package com.orca.app.data.network

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

data class PortInfo(
    val port: Int,
    val service: String,
)

data class PortScanResult(
    val host: String,
    val resolvedIp: String?,
    val openPorts: List<PortInfo>,
    val scannedCount: Int,
    val durationMs: Long,
)

@Singleton
class PortScannerRepository @Inject constructor() {

    val commonPorts: List<Int> = listOf(
        21, 22, 23, 25, 53, 80, 110, 111, 135, 139, 143, 443, 445,
        993, 995, 1433, 1521, 2049, 3306, 3389, 5432, 5900, 6379,
        8000, 8080, 8443, 8888, 9000, 27017,
    )

    suspend fun scan(
        host: String,
        ports: List<Int>,
        timeoutMs: Int = 2000,
    ): Result<PortScanResult> = withContext(Dispatchers.IO) {
        val trimmed = host.trim()
        if (trimmed.isBlank()) return@withContext Result.failure(IllegalArgumentException("Enter a hostname or IP"))

        val uniquePorts = ports.distinct().filter { it in 1..65535 }
        if (uniquePorts.isEmpty()) return@withContext Result.failure(IllegalArgumentException("No valid ports to scan"))
        if (uniquePorts.size > MAX_PORTS) {
            return@withContext Result.failure(
                IllegalArgumentException("Maximum $MAX_PORTS ports per scan (got ${uniquePorts.size})"),
            )
        }

        try {
            val start = System.currentTimeMillis()
            val resolvedIp = runCatching {
                java.net.InetAddress.getByName(trimmed).hostAddress
            }.getOrNull()

            val openPorts = coroutineScope {
                uniquePorts.map { port ->
                    async {
                        coroutineContext.ensureActive()
                        if (isPortOpen(trimmed, port, timeoutMs)) port else null
                    }
                }.awaitAll().filterNotNull().sorted().map { PortInfo(it, serviceName(it)) }
            }

            Result.success(
                PortScanResult(
                    host = trimmed,
                    resolvedIp = resolvedIp,
                    openPorts = openPorts,
                    scannedCount = uniquePorts.size,
                    durationMs = System.currentTimeMillis() - start,
                ),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Scan failed: ${e.message ?: "Unknown error"}"))
        }
    }

    private fun isPortOpen(host: String, port: Int, timeoutMs: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeoutMs)
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    fun parsePorts(input: String): List<Int> {
        return input.split(",", " ", ";")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 1..65535 }
            .distinct()
            .take(MAX_PORTS)
    }

    private fun serviceName(port: Int): String = when (port) {
        21 -> "FTP"
        22 -> "SSH"
        23 -> "Telnet"
        25 -> "SMTP"
        53 -> "DNS"
        80 -> "HTTP"
        110 -> "POP3"
        143 -> "IMAP"
        443 -> "HTTPS"
        445 -> "SMB"
        3306 -> "MySQL"
        3389 -> "RDP"
        5432 -> "PostgreSQL"
        5900 -> "VNC"
        6379 -> "Redis"
        8080 -> "HTTP-Alt"
        8443 -> "HTTPS-Alt"
        27017 -> "MongoDB"
        else -> "Unknown"
    }

    companion object {
        const val MAX_PORTS = 200
    }
}
