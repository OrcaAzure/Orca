package com.orca.app.domain.network

data class CidrResult(
    val cidr: String,
    val networkAddress: String,
    val broadcastAddress: String,
    val firstHost: String?,
    val lastHost: String?,
    val subnetMask: String,
    val wildcardMask: String,
    val prefixLength: Int,
    val totalAddresses: Long,
    val usableHosts: Long,
    val ipClass: String,
)

object CidrCalculator {

    fun calculate(cidr: String): CidrResult {
        val trimmed = cidr.trim()
        val parts = trimmed.split("/")
        if (parts.size != 2) throw IllegalArgumentException("Use format: 192.168.1.0/24")

        val ip = parts[0].trim()
        val prefix = parts[1].trim().toIntOrNull()
            ?: throw IllegalArgumentException("Invalid prefix length")

        if (prefix !in 0..32) throw IllegalArgumentException("Prefix must be between 0 and 32")

        val ipLong = IpAddressUtils.ipToLong(ip)
        val mask = IpAddressUtils.prefixToMask(prefix)
        val network = ipLong and mask
        val broadcast = network or mask.inv() and 0xFFFFFFFFL

        val totalAddresses = if (prefix == 32) 1L else 1L shl (32 - prefix)
        val usableHosts = when {
            prefix >= 31 -> totalAddresses
            prefix == 0 -> totalAddresses - 2
            else -> totalAddresses - 2
        }.coerceAtLeast(0)

        val firstHost = when {
            prefix >= 31 -> IpAddressUtils.longToIp(network)
            prefix == 0 -> null
            else -> IpAddressUtils.longToIp(network + 1)
        }

        val lastHost = when {
            prefix >= 31 -> IpAddressUtils.longToIp(broadcast)
            prefix == 0 -> null
            else -> IpAddressUtils.longToIp(broadcast - 1)
        }

        return CidrResult(
            cidr = trimmed,
            networkAddress = IpAddressUtils.longToIp(network),
            broadcastAddress = IpAddressUtils.longToIp(broadcast),
            firstHost = firstHost,
            lastHost = lastHost,
            subnetMask = IpAddressUtils.longToIp(mask),
            wildcardMask = IpAddressUtils.longToIp(mask.inv() and 0xFFFFFFFFL),
            prefixLength = prefix,
            totalAddresses = totalAddresses,
            usableHosts = usableHosts,
            ipClass = classifyIp(network),
        )
    }

    private fun classifyIp(network: Long): String {
        val firstOctet = ((network shr 24) and 0xFF).toInt()
        return when {
            firstOctet in 1..126 -> "Class A"
            firstOctet in 128..191 -> "Class B"
            firstOctet in 192..223 -> "Class C"
            firstOctet in 224..239 -> "Class D (Multicast)"
            else -> "Class E (Reserved)"
        }
    }
}
