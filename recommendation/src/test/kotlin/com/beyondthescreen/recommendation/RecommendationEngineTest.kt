package com.beyondthescreen.recommendation

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationEngineTest {
    private val configured = BedrockConfig(
        region = "us-east-1",
        modelId = "us.anthropic.claude-haiku-4-5-20251001-v1:0",
        accessKeyId = "AKIDEXAMPLE",
        secretAccessKey = "secret",
        sessionToken = null,
    )
    private val title = Catalog.byId("master-and-commander")

    @Test
    fun unconfiguredEngineDoesNotCallBedrock() = runBlocking {
        var calls = 0
        val engine = RecommendationEngine(BedrockConfig.unconfigured(), BedrockTransport { _, _ ->
            calls++
            ""
        })
        val result = engine.recommend(title)
        assertEquals(0, calls)
        assertEquals(Source.CURATED, result.source)
        assertTrue(result.note == null)
        assertTrue(result.categories.any { it.id == "books" })
    }

    @Test
    fun aValidModelReplyBecomesTheBedrockSet() = runBlocking {
        var seen = ""
        val engine = RecommendationEngine(configured, BedrockTransport { _, body ->
            seen = body
            envelope(modelJson())
        })
        val result = engine.recommend(title)
        assertTrue(seen.contains("Master and Commander"))
        assertTrue(seen.contains("affiliate"))
        assertEquals(Source.BEDROCK, result.source)
        assertEquals("Harbor coat", result.categories.first().suggestions.first().name)
        assertTrue(result.note == null)
    }

    @Test
    fun httpFailuresFallBackWithoutLosingTheTitle() = runBlocking {
        val engine = RecommendationEngine(configured, BedrockTransport { _, _ ->
            throw BedrockException("HTTP 403: AccessDeniedException")
        })
        val result = engine.recommend(title)
        assertEquals(Source.CURATED, result.source)
        assertEquals("master-and-commander", result.titleId)
        assertTrue(result.note!!.contains("curated"))
        assertTrue(result.detail!!.contains("403"))
        assertTrue(result.categories.any { it.id == "navigation" })
    }

    @Test
    fun unreadableModelTextFallsBack() = runBlocking {
        val engine = RecommendationEngine(configured, BedrockTransport { _, _ ->
            envelope("I cannot return JSON today.")
        })
        val result = engine.recommend(title)
        assertEquals(Source.CURATED, result.source)
        assertTrue(result.detail!!.contains("JSON"))
    }

    @Test
    fun promptForbidsAffiliateTagsAndMoreMovies() {
        assertTrue(Prompts.SYSTEM.contains("affiliate"))
        assertTrue(Prompts.SYSTEM.contains("Do not recommend other movies or shows"))
        assertTrue(Prompts.userPrompt(title).contains("napoleonic wars"))
    }

    @Test
    fun extractsAssistantTextFromAConverseEnvelope() {
        val text = BedrockResponses.extractAssistantText(envelope("hello deck"))
        assertEquals("hello deck", text)
    }

    private fun modelJson(): String = """
        {"categories":[
          {"id":"fashion","label":"Coats","suggestions":[
            {"name":"Harbor coat","why":"The deck uniform is a wool coat you can still wear ashore.","searchQuery":"wool harbor coat"},
            {"name":"Linen shirt","why":"The crew lives in shirts that survive salt water.","searchQuery":"linen shirt"}
          ]},
          {"id":"books","label":"Books","suggestions":[
            {"name":"Naval history","why":"The chase is a chapter of the Napoleonic wars at sea.","searchQuery":"napoleonic naval history"}
          ]}
        ]}
    """.trimIndent()

    private fun envelope(text: String): String {
        val payload = buildJsonObject {
            put(
                "output",
                buildJsonObject {
                    put(
                        "message",
                        buildJsonObject {
                            put("role", "assistant")
                            put(
                                "content",
                                buildJsonArray {
                                    add(buildJsonObject { put("text", text) })
                                },
                            )
                        },
                    )
                },
            )
        }
        return payload.toString()
    }
}
