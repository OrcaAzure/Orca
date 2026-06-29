package com.orca.app.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

data class PingResult(
    val host: String,
    val resolvedIp: String?,
    val packetsSent: Int,
    val packetsReceived: Int,
    val packetLossPercent: Int,
    val minRttMs: Double?,
    val avgRttMs: Double?,
    val maxRttMs: Double?,
    val output: String,
    val reachable: Boolean,
)

@Singleton
class PingRepository @Inject constructor() {

    suspend fun ping(host: String, count: Int = 4): Result<PingResult> = withContext(Dispatchers.IO) {
        val trimmed = host.trim()
        if (trimmed.isBlank()) return@withContext Result.failure(IllegalArgumentException("Enter a hostname or IP"))

        try {
            val resolvedIp = runCatching {
                InetAddress.getByName(trimmed).hostAddress
            }.getOrNull()

            val process = ProcessBuilder()
                .command("ping", "-c", count.toString(), "-W", "2", trimmed)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            val stats = parsePingStats(output)
            val transmitted = stats.transmitted ?: count
            val received = stats.received ?: if (exitCode == 0) count else 0
            val loss = if (transmitted > 0) ((transmitted - received) * 100 / transmitted) else 100

            Result.success(
                PingResult(
                    host = trimmed,
                    resolvedIp = resolvedIp,
                    packetsSent = transmitted,
                    packetsReceived = received,
                    packetLossPercent = loss,
                    minRttMs = stats.minRtt,
                    avgRttMs = stats.avgRtt,
                    maxRttMs = stats.maxRtt,
                    output = output.trim(),
                    reachable = received > 0,
                ),
            )
        } catch (e: Exception) {
            Result.failure(Exception("Ping failed: ${e.message ?: "Unknown error"}"))
        }
    }

    private data class PingStats(
        val transmitted: Int? = null,
        val received: Int? = null,
        val minRtt: Double? = null,
        val avgRtt: Double? = null,
        val maxRtt: Double? = null,
    )

    private fun parsePingStats(output: String): PingStats {
        var transmitted: Int? = null
        var received: Int? = null
        var minRtt: Double? = null
        var avgRtt: Double? = null
        var maxRtt: Double? = null

        val packetRegex = Regex("(\\d+) packets transmitted, (\\d+) received")
        packetRegex.find(output)?.let {
            transmitted = it.groupValues[1].toIntOrNull()
            received = it.groupValues[2].toIntOrNull()
        }

        val rttRegex = Regex("rtt min/avg/max(?:/\\w+)? = ([\\d.]+)/([\\d.]+)/([\\d.]+)")
        rttRegex.find(output)?.let {
            minRtt = it.groupValues[1].toDoubleOrNull()
            avgRtt = it.groupValues[2].toDoubleOrNull()
            maxRtt = it.groupValues[3].toDoubleOrNull()
        }

        return PingStats(transmitted, received, minRtt, avgRtt, maxRtt)
    }
}
