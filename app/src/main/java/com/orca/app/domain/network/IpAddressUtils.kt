package com.orca.app.domain.network

object IpAddressUtils {

    fun ipToLong(ip: String): Long {
        val parts = ip.trim().split(".")
        if (parts.size != 4) throw IllegalArgumentException("Invalid IP address")
        var result = 0L
        for (part in parts) {
            val octet = part.toIntOrNull() ?: throw IllegalArgumentException("Invalid IP address")
            if (octet !in 0..255) throw IllegalArgumentException("Invalid IP address")
            result = (result shl 8) or octet.toLong()
        }
        return result and 0xFFFFFFFFL
    }

    fun longToIp(value: Long): String {
        val v = value and 0xFFFFFFFFL
        return listOf(
            (v shr 24) and 0xFF,
            (v shr 16) and 0xFF,
            (v shr 8) and 0xFF,
            v and 0xFF,
        ).joinToString(".")
    }

    fun prefixToMask(prefix: Int): Long {
        require(prefix in 0..32) { "Prefix must be between 0 and 32" }
        if (prefix == 0) return 0L
        return (0xFFFFFFFFL shl (32 - prefix)) and 0xFFFFFFFFL
    }

    fun maskToPrefix(mask: Long): Int {
        val normalized = mask and 0xFFFFFFFFL
        if (normalized == 0L) return 0
        var prefix = 0
        var current = normalized
        while (current and 0x80000000L.toLong() != 0L) {
            prefix++
            current = current shl 1
        }
        if (current != 0L) throw IllegalArgumentException("Invalid subnet mask")
        return prefix
    }

    fun isValidIp(ip: String): Boolean = runCatching { ipToLong(ip) }.isSuccess

    fun isValidMask(mask: String): Boolean = runCatching { maskToPrefix(ipToLong(mask)) }.isSuccess
}
