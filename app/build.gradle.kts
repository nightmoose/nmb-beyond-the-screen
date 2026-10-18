import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val secretSources = Properties()
listOf("bedrock.properties", "local.properties").forEach { name ->
    val file = rootProject.file(name)
    if (file.exists()) file.inputStream().use { secretSources.load(it) }
}

fun bedrockProp(name: String): String {
    return (project.findProperty(name) as String?)
        ?: System.getenv(name)
        ?: secretSources.getProperty(name)
        ?: ""
}

fun javaString(value: String): String {
    val escaped = value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("$", "\${'$'}")
        .replace("\r", "")
        .replace("\n", "")
    return "\"$escaped\""
}

android {
    namespace = "com.beyondthescreen"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.beyondthescreen"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        buildConfigField("String", "BEDROCK_REGION", javaString(bedrockProp("BEDROCK_REGION").trim()))
        buildConfigField("String", "BEDROCK_MODEL_ID", javaString(bedrockProp("BEDROCK_MODEL_ID").trim()))
        buildConfigField("String", "BEDROCK_ACCESS_KEY_ID", javaString(bedrockProp("BEDROCK_ACCESS_KEY_ID").trim()))
        buildConfigField("String", "BEDROCK_SECRET_ACCESS_KEY", javaString(bedrockProp("BEDROCK_SECRET_ACCESS_KEY").trim()))
        buildConfigField("String", "BEDROCK_SESSION_TOKEN", javaString(bedrockProp("BEDROCK_SESSION_TOKEN").trim()))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    androidResources {
        noCompress += "mp4"
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/LICENSE.md",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/NOTICE.md",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":recommendation"))
    implementation(libs.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.animation)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.datasource)
    implementation(libs.zxing)
    testImplementation(libs.junit)
}
