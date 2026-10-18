package com.beyondthescreen.recommendation

import kotlinx.coroutines.CancellationException

class RecommendationEngine(
    private val config: BedrockConfig,
    private val transport: BedrockTransport,
    private val fallback: (Title) -> RecommendationSet = { FallbackCatalog.forTitle(it) },
    private val logger: (String) -> Unit = {},
) {
    suspend fun recommend(title: Title): RecommendationSet {
        if (!config.configured) {
            return fallback(title)
        }
        return try {
            val raw = transport.converse(config, Prompts.requestBody(title))
            val text = BedrockResponses.extractAssistantText(raw)
            when (val parsed = RecommendationParser.parse(text, title.id)) {
                is ParseOutcome.Ok -> parsed.set.copy(source = Source.BEDROCK, note = null, detail = null)
                is ParseOutcome.Failed -> curated(title, "Showing curated picks.", parsed.reason)
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            val detail = redact(error.message ?: error.javaClass.simpleName)
            logger("Bedrock request failed: $detail")
            curated(title, "Bedrock didn't answer. Showing curated picks.", detail)
        }
    }

    private fun curated(title: Title, note: String, detail: String): RecommendationSet =
        fallback(title).copy(note = note, detail = detail)
}
