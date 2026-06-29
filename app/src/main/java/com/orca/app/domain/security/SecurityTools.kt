package com.orca.app.domain.security

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object PasswordGenerator {
    private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#\$%^&*()-_=+[]{}|;:,.<>?"

    fun generate(
        length: Int,
        includeUpper: Boolean = true,
        includeLower: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true,
    ): String {
        require(length in 4..128) { "Length must be between 4 and 128" }
        val pool = buildString {
            if (includeUpper) append(UPPER)
            if (includeLower) append(LOWER)
            if (includeDigits) append(DIGITS)
            if (includeSymbols) append(SYMBOLS)
        }
        if (pool.isEmpty()) throw IllegalArgumentException("Select at least one character set")

        return (1..length).map { pool[Random.nextInt(pool.length)] }.joinToString("")
    }
}

object RotCipher {
    fun transform(text: String, shift: Int): String {
        val normalizedShift = ((shift % 26) + 26) % 26
        return text.map { char ->
            when {
                char in 'A'..'Z' -> 'A' + (char - 'A' + normalizedShift) % 26
                char in 'a'..'z' -> 'a' + (char - 'a' + normalizedShift) % 26
                else -> char
            }
        }.joinToString("")
    }
}

object XorCipher {
    fun encodeToHex(text: String, key: String): String {
        require(key.isNotEmpty()) { "Key cannot be empty" }
        val bytes = text.toByteArray(Charsets.UTF_8)
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        return bytes.mapIndexed { i, byte ->
            (byte.toInt() xor keyBytes[i % keyBytes.size].toInt()) and 0xFF
        }.joinToString("") { "%02x".format(it) }
    }

    fun decodeFromHex(hex: String, key: String): String {
        require(key.isNotEmpty()) { "Key cannot be empty" }
        val cleaned = hex.trim().replace(" ", "")
        if (cleaned.length % 2 != 0) throw IllegalArgumentException("Invalid hex input")
        val bytes = cleaned.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val decoded = bytes.mapIndexed { i, byte ->
            (byte.toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }.toByteArray()
        return String(decoded, Charsets.UTF_8)
    }
}

enum class HmacAlgorithm(val label: String, val algorithm: String) {
    SHA256("HMAC-SHA256", "HmacSHA256"),
    SHA512("HMAC-SHA512", "HmacSHA512"),
}

object HmacTool {
    fun compute(input: String, key: String, algorithm: HmacAlgorithm): String {
        val mac = Mac.getInstance(algorithm.algorithm)
        mac.init(SecretKeySpec(key.toByteArray(Charsets.UTF_8), algorithm.algorithm))
        val bytes = mac.doFinal(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
