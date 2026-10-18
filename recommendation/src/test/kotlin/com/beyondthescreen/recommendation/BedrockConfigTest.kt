package com.beyondthescreen.recommendation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BedrockConfigTest {
    @Test
    fun blankKeysStayOffline() {
        val config = BedrockConfig.fromMap(
            mapOf(
                "BEDROCK_ACCESS_KEY_ID" to "  ",
                "BEDROCK_SECRET_ACCESS_KEY" to "",
            ),
        )
        assertFalse(config.configured)
        assertEquals(BedrockConfig.DEFAULT_REGION, config.region)
        assertEquals(BedrockConfig.DEFAULT_MODEL_ID, config.modelId)
        assertNull(config.sessionToken)
    }

    @Test
    fun bothKeysEnableTheClient() {
        val config = BedrockConfig.fromMap(
            mapOf(
                "BEDROCK_REGION" to "us-west-2",
                "BEDROCK_MODEL_ID" to "amazon.nova-lite-v1:0",
                "BEDROCK_ACCESS_KEY_ID" to "AKIDEXAMPLE",
                "BEDROCK_SECRET_ACCESS_KEY" to "secret",
                "BEDROCK_SESSION_TOKEN" to "session",
            ),
        )
        assertTrue(config.configured)
        assertEquals("us-west-2", config.region)
        assertEquals("session", config.sessionToken)
        assertEquals("nova-lite-v1:0", config.modelLabel())
    }

    @Test
    fun rejectsRegionsAndModelsThatCouldChangeTheRequest() {
        val config = BedrockConfig.fromMap(
            mapOf(
                "BEDROCK_REGION" to "us-east-1/../",
                "BEDROCK_MODEL_ID" to "model/with/slash",
                "BEDROCK_ACCESS_KEY_ID" to "A",
                "BEDROCK_SECRET_ACCESS_KEY" to "B",
            ),
        )
        assertFalse(config.configured)
    }

    @Test
    fun shortensClaudeInferenceProfileIds() {
        val config = BedrockConfig(
            region = "us-east-1",
            modelId = "us.anthropic.claude-haiku-4-5-20251001-v1:0",
            accessKeyId = "A",
            secretAccessKey = "B",
            sessionToken = null,
        )
        assertEquals("claude-haiku-4-5", config.modelLabel())
    }
}
