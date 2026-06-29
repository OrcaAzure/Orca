package com.orca.app.domain.developer

import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder

object JsonTool {
    fun format(json: String): String {
        val trimmed = json.trim()
        return try {
            when {
                trimmed.startsWith("[") -> JSONArray(trimmed).toString(2)
                trimmed.startsWith("{") -> JSONObject(trimmed).toString(2)
                else -> throw IllegalArgumentException("Input must be a JSON object or array")
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JSON: ${e.message}")
        }
    }

    fun minify(json: String): String {
        val trimmed = json.trim()
        return try {
            when {
                trimmed.startsWith("[") -> JSONArray(trimmed).toString()
                trimmed.startsWith("{") -> JSONObject(trimmed).toString()
                else -> throw IllegalArgumentException("Input must be a JSON object or array")
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JSON: ${e.message}")
        }
    }
}

object UrlTool {
    fun encode(input: String): String = URLEncoder.encode(input, Charsets.UTF_8.name())

    fun decode(input: String): String = URLDecoder.decode(input, Charsets.UTF_8.name())
}
