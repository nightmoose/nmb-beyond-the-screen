package com.beyondthescreen.recommendation

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object BedrockResponses {
    private val json = Json { ignoreUnknownKeys = true }

    fun extractAssistantText(raw: String): String {
        val root = json.parseToJsonElement(raw).jsonObject
        val content = root["output"]
            ?.jsonObject
            ?.get("message")
            ?.jsonObject
            ?.get("content")
            ?: throw IllegalArgumentException("Bedrock response has no message content")
        if (content !is JsonArray) {
            throw IllegalArgumentException("Bedrock content was not a list")
        }
        val text = content.mapNotNull { block ->
            val value = block.jsonObject["text"] ?: return@mapNotNull null
            val primitive = value as? JsonPrimitive ?: return@mapNotNull null
            if (!primitive.isString) return@mapNotNull null
            primitive.jsonPrimitive.content
        }.joinToString("\n").trim()
        if (text.isBlank()) throw IllegalArgumentException("Bedrock message was empty")
        return text
    }
}

internal fun redact(message: String): String {
    val cleaned = message
        .replace(Regex("(?i)(authorization|secret|password|token|signature)[=:][^\\s,]+"), "$1=[redacted]")
        .replace(Regex("\\s+"), " ")
        .trim()
    return cleaned.take(160)
}
