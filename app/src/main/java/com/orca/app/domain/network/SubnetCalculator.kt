package com.orca.app.domain.network

data class SubnetResult(
    val ipAddress: String,
    val subnetMask: String,
    val prefixLength: Int,
    val networkAddress: String,
    val broadcastAddress: String,
    val firstHost: String?,
    val lastHost: String?,
    val wildcardMask: String,
    val totalAddresses: Long,
    val usableHosts: Long,
    val ipBinary: String,
    val maskBinary: String,
    val networkBinary: String,
)

object SubnetCalculator {

    fun calculate(ipAddress: String, subnetMask: String): SubnetResult {
        val ip = ipAddress.trim()
        val maskStr = subnetMask.trim()

        if (!IpAddressUtils.isValidIp(ip)) throw IllegalArgumentException("Invalid IP address")
        if (!IpAddressUtils.isValidMask(maskStr)) throw IllegalArgumentException("Invalid subnet mask")

        val ipLong = IpAddressUtils.ipToLong(ip)
        val mask = IpAddressUtils.ipToLong(maskStr)
        val prefix = IpAddressUtils.maskToPrefix(mask)

        val network = ipLong and mask
        val broadcast = network or mask.inv() and 0xFFFFFFFFL
        val totalAddresses = if (prefix == 32) 1L else 1L shl (32 - prefix)
        val usableHosts = when {
            prefix >= 31 -> totalAddresses
            else -> (totalAddresses - 2).coerceAtLeast(0)
        }

        val firstHost = when {
            prefix >= 31 -> IpAddressUtils.longToIp(network)
            else -> IpAddressUtils.longToIp(network + 1)
        }

        val lastHost = when {
            prefix >= 31 -> IpAddressUtils.longToIp(broadcast)
            else -> IpAddressUtils.longToIp(broadcast - 1)
        }

        return SubnetResult(
            ipAddress = ip,
            subnetMask = maskStr,
            prefixLength = prefix,
            networkAddress = IpAddressUtils.longToIp(network),
            broadcastAddress = IpAddressUtils.longToIp(broadcast),
            firstHost = firstHost,
            lastHost = lastHost,
            wildcardMask = IpAddressUtils.longToIp(mask.inv() and 0xFFFFFFFFL),
            totalAddresses = totalAddresses,
            usableHosts = usableHosts,
            ipBinary = toBinary(ipLong),
            maskBinary = toBinary(mask),
            networkBinary = toBinary(network),
        )
    }

    private fun toBinary(value: Long): String {
        val v = value and 0xFFFFFFFFL
        return (31 downTo 0).joinToString(".") { bit ->
            ((v shr bit) and 1).toString()
        }
    }
}
