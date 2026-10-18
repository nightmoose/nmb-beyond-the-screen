package com.beyondthescreen.recommendation

data class BedrockConfig(
    val region: String,
    val modelId: String,
    val accessKeyId: String,
    val secretAccessKey: String,
    val sessionToken: String?,
) {
    val configured: Boolean
        get() = accessKeyId.isNotBlank() &&
            secretAccessKey.isNotBlank() &&
            REGION.matches(region) &&
            MODEL.matches(modelId)

    fun modelLabel(): String {
        val leaf = modelId.substringAfterLast('.')
        val trimmed = DATE_SUFFIX.replace(leaf, "")
        return trimmed.ifBlank { leaf }.take(48)
    }

    companion object {
        const val DEFAULT_REGION = "us-east-1"
        const val DEFAULT_MODEL_ID = "us.anthropic.claude-haiku-4-5-20251001-v1:0"

        fun fromMap(values: Map<String, String>): BedrockConfig {
            fun read(key: String): String = values[key]?.trim().orEmpty()
            val region = read("BEDROCK_REGION").ifBlank { DEFAULT_REGION }
            val model = read("BEDROCK_MODEL_ID").ifBlank { DEFAULT_MODEL_ID }
            return BedrockConfig(
                region = region,
                modelId = model,
                accessKeyId = read("BEDROCK_ACCESS_KEY_ID"),
                secretAccessKey = read("BEDROCK_SECRET_ACCESS_KEY"),
                sessionToken = read("BEDROCK_SESSION_TOKEN").ifBlank { null },
            )
        }

        fun unconfigured(): BedrockConfig = fromMap(emptyMap())
    }
}

private val REGION = Regex("^[a-z0-9-]{2,32}$")
private val MODEL = Regex("^[A-Za-z0-9._:-]{3,200}$")
private val DATE_SUFFIX = Regex("-20\\d{6}.*$")
