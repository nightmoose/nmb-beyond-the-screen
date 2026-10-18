package com.beyondthescreen.recommendation

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class AwsSignature(
    val authorization: String,
    val payloadHash: String,
    val canonicalRequest: String,
    val signedHeaders: String,
)

class AwsSigV4(
    private val accessKeyId: String,
    private val secretAccessKey: String,
    private val region: String,
    private val service: String,
) {
    fun sign(
        method: String,
        host: String,
        canonicalUri: String,
        body: String,
        amzDate: String,
        contentType: String? = null,
        queryString: String = "",
        sessionToken: String? = null,
        includePayloadHashHeader: Boolean = true,
    ): AwsSignature {
        val payloadHash = sha256Hex(body)
        val headers = linkedMapOf<String, String>()
        if (!contentType.isNullOrBlank()) headers["content-type"] = contentType
        headers["host"] = host
        if (includePayloadHashHeader) headers["x-amz-content-sha256"] = payloadHash
        headers["x-amz-date"] = amzDate
        if (!sessionToken.isNullOrBlank()) headers["x-amz-security-token"] = sessionToken

        val names = headers.keys.sorted()
        val canonicalHeaders = names.joinToString("") { name ->
            "$name:${canonicalValue(headers.getValue(name))}\n"
        }
        val signed = names.joinToString(";")
        val canonicalRequest = listOf(
            method.uppercase(Locale.US),
            canonicalUri.ifBlank { "/" },
            queryString,
            canonicalHeaders,
            signed,
            payloadHash,
        ).joinToString("\n")

        val dateStamp = amzDate.take(8)
        val scope = "$dateStamp/$region/$service/aws4_request"
        val stringToSign = listOf(
            ALGORITHM,
            amzDate,
            scope,
            sha256Hex(canonicalRequest),
        ).joinToString("\n")
        val signature = hmac(signingKey(dateStamp), stringToSign).toHex()
        val authorization = "$ALGORITHM Credential=$accessKeyId/$scope, SignedHeaders=$signed, Signature=$signature"
        return AwsSignature(authorization, payloadHash, canonicalRequest, signed)
    }

    private fun signingKey(dateStamp: String): ByteArray {
        val dateKey = hmac("AWS4$secretAccessKey".toByteArray(Charsets.UTF_8), dateStamp)
        val regionKey = hmac(dateKey, region)
        val serviceKey = hmac(regionKey, service)
        return hmac(serviceKey, "aws4_request")
    }

    private fun canonicalValue(value: String): String = value.trim().replace(Regex("\\s+"), " ")
}

internal fun encodePath(path: String): String {
    if (path.isEmpty() || path == "/") return "/"
    val leading = path.startsWith("/")
    val parts = path.split("/").filter { it.isNotEmpty() }.map { encodeRfc3986(it) }
    return (if (leading) "/" else "") + parts.joinToString("/")
}

internal fun encodeRfc3986(value: String): String {
    val out = StringBuilder(value.length)
    for (byte in value.toByteArray(Charsets.UTF_8)) {
        val unsigned = byte.toInt() and 0xff
        val unreserved = unsigned in 'A'.code..'Z'.code ||
            unsigned in 'a'.code..'z'.code ||
            unsigned in '0'.code..'9'.code ||
            unsigned == '-'.code ||
            unsigned == '_'.code ||
            unsigned == '.'.code ||
            unsigned == '~'.code
        if (unreserved) {
            out.append(unsigned.toChar())
        } else {
            out.append('%')
            out.append(HEX_UPPER[unsigned shr 4])
            out.append(HEX_UPPER[unsigned and 0x0f])
        }
    }
    return out.toString()
}

internal fun converseUrl(region: String, modelId: String): String {
    val path = encodePath("/model/$modelId/converse")
    return "https://bedrock-runtime.$region.amazonaws.com$path"
}

internal fun amzNow(): String {
    val format = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(Date())
}

private fun sha256Hex(data: String): String =
    MessageDigest.getInstance("SHA-256").digest(data.toByteArray(Charsets.UTF_8)).toHex()

private fun hmac(key: ByteArray, data: String): ByteArray {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(key, "HmacSHA256"))
    return mac.doFinal(data.toByteArray(Charsets.UTF_8))
}

private fun ByteArray.toHex(): String = joinToString("") { byte ->
    val unsigned = byte.toInt() and 0xff
    "${HEX[unsigned shr 4]}${HEX[unsigned and 0x0f]}"
}

private const val ALGORITHM = "AWS4-HMAC-SHA256"
private val HEX = "0123456789abcdef".toCharArray()
private val HEX_UPPER = "0123456789ABCDEF".toCharArray()
