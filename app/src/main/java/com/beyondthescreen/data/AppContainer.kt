package com.beyondthescreen.data

import android.content.Context
import com.beyondthescreen.BuildConfig
import com.beyondthescreen.recommendation.BedrockConfig
import com.beyondthescreen.recommendation.Catalog
import com.beyondthescreen.recommendation.OkHttpBedrockTransport
import com.beyondthescreen.recommendation.RecommendationEngine
import com.beyondthescreen.recommendation.Title
import java.io.File
import java.util.Properties

class AppContainer(
    val config: BedrockConfig,
    logger: (String) -> Unit,
) {
    val catalog: List<Title> = Catalog.all
    val engine = RecommendationEngine(
        config = config,
        transport = OkHttpBedrockTransport(),
        logger = logger,
    )
}

fun loadBedrockConfig(context: Context): BedrockConfig {
    val values = linkedMapOf<String, String>()
    fun put(key: String, value: String?) {
        val trimmed = value?.trim().orEmpty()
        if (trimmed.isNotEmpty()) values[key] = trimmed
    }
    put("BEDROCK_REGION", BuildConfig.BEDROCK_REGION)
    put("BEDROCK_MODEL_ID", BuildConfig.BEDROCK_MODEL_ID)
    put("BEDROCK_ACCESS_KEY_ID", BuildConfig.BEDROCK_ACCESS_KEY_ID)
    put("BEDROCK_SECRET_ACCESS_KEY", BuildConfig.BEDROCK_SECRET_ACCESS_KEY)
    put("BEDROCK_SESSION_TOKEN", BuildConfig.BEDROCK_SESSION_TOKEN)
    val files = listOfNotNull(
        File(context.filesDir, "bedrock.properties"),
        context.getExternalFilesDir(null)?.let { File(it, "bedrock.properties") },
    )
    files.filter { it.isFile }.forEach { file ->
        val props = Properties()
        file.inputStream().use { props.load(it) }
        props.stringPropertyNames().forEach { key -> put(key, props.getProperty(key)) }
    }
    return BedrockConfig.fromMap(values)
}
