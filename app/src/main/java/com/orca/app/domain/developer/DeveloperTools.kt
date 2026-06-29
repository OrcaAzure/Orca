package com.orca.app.domain.developer

import android.util.Base64

object Base64Tool {
    fun encode(input: String): String =
        Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

    fun decode(input: String): String {
        val bytes = Base64.decode(input.trim(), Base64.DEFAULT)
        return String(bytes, Charsets.UTF_8)
    }
}

object HexTool {
    fun encode(input: String): String =
        input.toByteArray(Charsets.UTF_8).joinToString("") { "%02x".format(it) }

    fun decode(input: String): String {
        val cleaned = input.trim().replace(" ", "").replace("0x", "")
        if (cleaned.length % 2 != 0) throw IllegalArgumentException("Invalid hex string length")
        val bytes = cleaned.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        return String(bytes, Charsets.UTF_8)
    }
}

enum class HashAlgorithm(val label: String, val algorithm: String) {
    MD5("MD5", "MD5"),
    SHA1("SHA-1", "SHA-1"),
    SHA256("SHA-256", "SHA-256"),
    SHA512("SHA-512", "SHA-512"),
}

object HashTool {
    fun hash(input: String, algorithm: HashAlgorithm): String {
        val digest = java.security.MessageDigest.getInstance(algorithm.algorithm)
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

data class JwtDecodeResult(
    val header: String,
    val payload: String,
    val signature: String,
)

object JwtTool {
    fun decode(token: String): JwtDecodeResult {
        val parts = token.trim().split(".")
        if (parts.size < 2) throw IllegalArgumentException("Invalid JWT format")

        val header = decodePart(parts[0])
        val payload = decodePart(parts[1])
        val signature = if (parts.size > 2) parts[2] else ""

        return JwtDecodeResult(header = header, payload = payload, signature = signature)
    }

    private fun decodePart(part: String): String {
        val padded = part + "=".repeat((4 - part.length % 4) % 4)
        val bytes = Base64.decode(padded, Base64.URL_SAFE or Base64.NO_WRAP)
        return String(bytes, Charsets.UTF_8)
    }
}
