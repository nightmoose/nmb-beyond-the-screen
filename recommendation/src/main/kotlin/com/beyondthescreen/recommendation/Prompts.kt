package com.beyondthescreen.recommendation

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object Prompts {
    const val SYSTEM: String = """
You recommend real products and experiences from retail categories other than streaming video.
Given a film or show, suggest things a viewer could buy or book next: books, fashion, tools, food, music, hobbies, home goods, and outings.
Do not recommend other movies or shows.
Do not invent ASINs, prices, or reviews.
Do not include affiliate tags, tracking parameters, or links.
Return only JSON with this shape:
{"categories":[{"id":"books","label":"Books","suggestions":[{"name":"short product name","why":"one sentence","searchQuery":"concrete amazon search"}]}]}
Use 4 to 6 categories and 3 suggestions each.
The why must be one sentence under 140 characters that ties the item to a specific detail of the title.
searchQuery must be something a shopper would type, with no quotes and no affiliate tag.
"""

    fun userPrompt(title: Title): String = buildString {
        appendLine("Title: ${title.name}")
        appendLine("Year: ${title.year}")
        appendLine("Kind: ${title.kind}")
        appendLine("Logline: ${title.logline}")
        appendLine("Synopsis: ${title.synopsis}")
        append("Themes: ${title.themes.joinToString(", ")}")
    }

    fun requestBody(title: Title): String {
        val payload = buildJsonObject {
            put(
                "system",
                buildJsonArray {
                    add(buildJsonObject { put("text", SYSTEM.trim()) })
                },
            )
            put(
                "messages",
                buildJsonArray {
                    add(
                        buildJsonObject {
                            put("role", "user")
                            put(
                                "content",
                                buildJsonArray {
                                    add(buildJsonObject { put("text", userPrompt(title)) })
                                },
                            )
                        },
                    )
                },
            )
            put(
                "inferenceConfig",
                buildJsonObject {
                    put("maxTokens", 2400)
                    put("temperature", 0.3)
                },
            )
        }
        return Json.encodeToString(JsonElement.serializer(), payload)
    }
}
