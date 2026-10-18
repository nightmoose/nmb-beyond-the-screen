package com.beyondthescreen.recommendation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

fun interface BedrockTransport {
    suspend fun converse(config: BedrockConfig, body: String): String
}

class BedrockException(message: String) : Exception(message)

class OkHttpBedrockTransport : BedrockTransport {
    private val client: OkHttpClient = defaultClient()
    override suspend fun converse(config: BedrockConfig, body: String): String = withContext(Dispatchers.IO) {
        val amzDate = amzNow()
        val path = encodePath("/model/${config.modelId}/converse")
        val host = "bedrock-runtime.${config.region}.amazonaws.com"
        val signed = AwsSigV4(
            accessKeyId = config.accessKeyId,
            secretAccessKey = config.secretAccessKey,
            region = config.region,
            service = "bedrock",
        ).sign(
            method = "POST",
            host = host,
            canonicalUri = path,
            body = body,
            amzDate = amzDate,
            contentType = "application/json",
            sessionToken = config.sessionToken,
            includePayloadHashHeader = true,
        )
        val request = Request.Builder()
            .url("https://$host$path")
            .post(body.toRequestBody(JSON))
            .header("Accept", "application/json")
            .header("X-Amz-Date", amzDate)
            .header("X-Amz-Content-Sha256", signed.payloadHash)
            .header("Authorization", signed.authorization)
            .apply {
                if (!config.sessionToken.isNullOrBlank()) {
                    header("X-Amz-Security-Token", config.sessionToken)
                }
            }
            .build()
        client.newCall(request).execute().use { response ->
            val payload = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw BedrockException("HTTP ${response.code}: ${redact(payload)}")
            }
            if (payload.isBlank()) throw BedrockException("HTTP ${response.code}: empty body")
            payload
        }
    }

    companion object {
        private val JSON = "application/json".toMediaType()

        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .callTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }
}
