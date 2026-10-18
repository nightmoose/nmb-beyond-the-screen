package com.beyondthescreen.recommendation

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

sealed class ParseOutcome {
    data class Ok(val set: RecommendationSet) : ParseOutcome()
    data class Failed(val reason: String) : ParseOutcome()
}

object RecommendationParser {
    private val json = Json { ignoreUnknownKeys = true }
    private val bannedLabels = setOf("movies", "movie", "tv", "tv shows", "streaming", "films", "film")

    fun parse(text: String, titleId: String): ParseOutcome {
        val payload = try {
            val raw = extractJson(text)
            val element = json.parseToJsonElement(raw)
            when (element) {
                is JsonArray -> buildJsonObject { put("categories", element) }
                is JsonObject -> element
                else -> return ParseOutcome.Failed("Model text was not JSON.")
            }
        } catch (error: Exception) {
            return ParseOutcome.Failed("Could not read JSON from the model.")
        }

        val categories = payload["categories"]?.let { value ->
            if (value is JsonArray) value else null
        } ?: return ParseOutcome.Failed("JSON has no categories.")

        val parsed = mutableListOf<Category>()
        val seen = mutableSetOf<String>()
        for (node in categories) {
            if (parsed.size >= MAX_CATEGORIES) break
            val obj = node as? JsonObject ?: continue
            val label = stringField(obj, "label")
            if (label.lowercase() in bannedLabels) continue
            val id = slug(stringField(obj, "id").ifBlank { label }).ifBlank { "more" }
            val uniqueId = unique(id, seen)
            val suggestions = obj["suggestions"]?.let { value ->
                if (value is JsonArray) value else null
            } ?: continue
            val items = mutableListOf<Suggestion>()
            for (suggestion in suggestions) {
                if (items.size >= MAX_SUGGESTIONS) break
                val item = suggestion as? JsonObject ?: continue
                val name = oneLine(stringField(item, "name"), MAX_NAME)
                val why = oneLine(stringField(item, "why"), MAX_WHY)
                val query = SearchLinks.sanitizeQuery(stringField(item, "searchQuery"))
                if (name.length < 2 || why.length < 12 || query.length < 2) continue
                items += Suggestion(name, why, query)
            }
            if (items.isEmpty()) continue
            parsed += Category(uniqueId, label.ifBlank { uniqueId }, items)
        }

        if (parsed.size < MIN_CATEGORIES) {
            return ParseOutcome.Failed("Not enough usable categories.")
        }
        return ParseOutcome.Ok(
            RecommendationSet(
                titleId = titleId,
                source = Source.BEDROCK,
                categories = parsed,
            ),
        )
    }

    internal fun extractJson(text: String): String {
        val start = text.indexOf('{').takeIf { it >= 0 } ?: text.indexOf('[')
        if (start < 0) throw IllegalArgumentException("No JSON")
        val opener = text[start]
        val closer = if (opener == '{') '}' else ']'
        var depth = 0
        var inString = false
        var escaped = false
        for (index in start until text.length) {
            val char = text[index]
            if (inString) {
                if (escaped) {
                    escaped = false
                } else if (char == '\\') {
                    escaped = true
                } else if (char == '"') {
                    inString = false
                }
                continue
            }
            when (char) {
                '"' -> inString = true
                opener -> depth++
                closer -> {
                    depth--
                    if (depth == 0) return text.substring(start, index + 1)
                }
            }
        }
        throw IllegalArgumentException("Unbalanced JSON")
    }

    private fun stringField(obj: JsonObject, name: String): String {
        val primitive = obj[name] as? JsonPrimitive ?: return ""
        return if (primitive.isString) primitive.content else ""
    }

    private fun oneLine(raw: String, max: Int): String =
        raw.replace(Regex("[\\r\\n]+"), " ").replace(Regex("\\s+"), " ").trim().take(max)

    private fun slug(raw: String): String =
        raw.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')

    private fun unique(id: String, seen: MutableSet<String>): String {
        var candidate = id
        var suffix = 2
        while (!seen.add(candidate)) {
            candidate = "$id-$suffix"
            suffix++
        }
        return candidate
    }

    private const val MIN_CATEGORIES = 2
    private const val MAX_CATEGORIES = 6
    private const val MAX_SUGGESTIONS = 4
    private const val MAX_NAME = 80
    private const val MAX_WHY = 160
}
