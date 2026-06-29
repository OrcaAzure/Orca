package com.orca.app.domain.tools

import com.orca.app.navigation.Routes
import com.orca.app.ui.theme.DeveloperAccent
import com.orca.app.ui.theme.DeviceAccent
import com.orca.app.ui.theme.NetworkAccent
import com.orca.app.ui.theme.SecurityAccent
import androidx.compose.ui.graphics.Color

data class OrcaTool(
    val id: String,
    val route: String,
    val category: String,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    val keywords: List<String> = emptyList(),
)

object ToolRegistry {

    val allTools: List<OrcaTool> = listOf(
        // Network
        tool("ping", Routes.NETWORK_PING, "Network", "📡", "Ping", "Test host reachability", NetworkAccent, "icmp reachability latency"),
        tool("dns", Routes.NETWORK_DNS, "Network", "🔍", "DNS Lookup", "A, AAAA, MX, TXT, CNAME, NS", NetworkAccent, "dns txt mx cname records dig"),
        tool("http", Routes.NETWORK_HTTP_HEADERS, "Network", "📋", "HTTP Headers", "Inspect response headers", NetworkAccent, "http headers request curl"),
        tool("port", Routes.NETWORK_PORT_SCANNER, "Network", "🔌", "Port Scanner", "TCP scan common CTF ports", NetworkAccent, "nmap port scan tcp"),
        tool("ssl", Routes.NETWORK_SSL, "Network", "🔒", "SSL Inspector", "Certificate & SAN inspection", NetworkAccent, "tls certificate x509 san"),
        tool("whois", Routes.NETWORK_WHOIS, "Network", "📇", "WHOIS", "Domain registration lookup", NetworkAccent, "whois domain osint"),
        tool("cidr", Routes.NETWORK_CIDR, "Network", "🧮", "CIDR Calculator", "Parse CIDR notation", NetworkAccent, "cidr subnet mask offline"),
        tool("subnet", Routes.NETWORK_SUBNET, "Network", "🔢", "Subnet Calculator", "IP + mask calculations", NetworkAccent, "subnet ip mask offline"),

        // Developer
        tool("base64", Routes.DEVELOPER_BASE64, "Developer", "🔤", "Base64", "Encode & decode", DeveloperAccent, "base64 encode decode offline"),
        tool("hex", Routes.DEVELOPER_HEX, "Developer", "🔢", "Hex", "Hex encode & decode", DeveloperAccent, "hexadecimal encode decode offline"),
        tool("hash", Routes.DEVELOPER_HASH, "Developer", "#️⃣", "Hash", "MD5, SHA-1, SHA-256, SHA-512", DeveloperAccent, "hash md5 sha256 crack offline"),
        tool("jwt", Routes.DEVELOPER_JWT, "Developer", "🎫", "JWT Decoder", "Decode token header & payload", DeveloperAccent, "jwt token decode offline"),
        tool("json", Routes.DEVELOPER_JSON, "Developer", "{ }", "JSON Formatter", "Pretty-print & minify JSON", DeveloperAccent, "json format beautify minify offline"),
        tool("url", Routes.DEVELOPER_URL, "Developer", "🔗", "URL Encode", "URL encode & decode", DeveloperAccent, "url encode decode percent offline"),

        // Security
        tool("password", Routes.SECURITY_PASSWORD, "Security", "🔑", "Password Generator", "Secure random passwords", SecurityAccent, "password generate random offline"),
        tool("rot", Routes.SECURITY_ROT, "Security", "🔄", "ROT Cipher", "Caesar cipher encode/decode", SecurityAccent, "rot caesar cipher shift offline"),
        tool("xor", Routes.SECURITY_XOR, "Security", "⊕", "XOR Cipher", "XOR with key — hex output", SecurityAccent, "xor cipher crypto offline"),
        tool("hmac", Routes.SECURITY_HMAC, "Security", "✍️", "HMAC", "HMAC-SHA256 & SHA-512", SecurityAccent, "hmac signature mac offline"),

        // Device
        tool("device-info", Routes.DEVICE_INFO, "Device", "📱", "Device Info", "Model, Android version, ABI", DeviceAccent, "device model android version offline"),
        tool("network-info", Routes.DEVICE_NETWORK, "Device", "📶", "Network Info", "Connection type & local IPs", DeviceAccent, "wifi ip address network offline"),
        tool("system-status", Routes.DEVICE_SYSTEM, "Device", "⚡", "System Status", "Battery, storage & memory", DeviceAccent, "battery storage memory ram offline"),
    )

    fun search(query: String): List<OrcaTool> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return allTools
        return allTools.filter { tool ->
            tool.title.lowercase().contains(q) ||
                tool.subtitle.lowercase().contains(q) ||
                tool.category.lowercase().contains(q) ||
                tool.keywords.any { it.contains(q) }
        }
    }

    fun findById(id: String): OrcaTool? = allTools.find { it.id == id }

    fun findByRoute(route: String): OrcaTool? = allTools.find { it.route == route }

    private fun tool(
        id: String,
        route: String,
        category: String,
        emoji: String,
        title: String,
        subtitle: String,
        accent: Color,
        keywords: String,
    ) = OrcaTool(
        id = id,
        route = route,
        category = category,
        emoji = emoji,
        title = title,
        subtitle = subtitle,
        accentColor = accent,
        keywords = keywords.split(" "),
    )
}
