package com.beyondthescreen.recommendation

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AwsSigV4Test {
    private val signer = AwsSigV4(
        accessKeyId = "AKIDEXAMPLE",
        secretAccessKey = "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY",
        region = "us-east-1",
        service = "service",
    )

    @Test
    fun matchesTheOfficialGetVanillaVector() {
        val signed = signer.sign(
            method = "GET",
            host = "example.amazonaws.com",
            canonicalUri = "/",
            body = "",
            amzDate = "20150830T123600Z",
            includePayloadHashHeader = false,
        )
        assertEquals(
            "AWS4-HMAC-SHA256 Credential=AKIDEXAMPLE/20150830/us-east-1/service/aws4_request, " +
                "SignedHeaders=host;x-amz-date, " +
                "Signature=5fa00fa31553b73ebf1942676e86291e8372ff2a2260956d9b8aae1d763fbf31",
            signed.authorization,
        )
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", signed.payloadHash)
    }

    @Test
    fun matchesTheOfficialPostVanillaVector() {
        val signed = signer.sign(
            method = "POST",
            host = "example.amazonaws.com",
            canonicalUri = "/",
            body = "",
            amzDate = "20150830T123600Z",
            includePayloadHashHeader = false,
        )
        assertEquals(
            "5da7c1a2acd57cee7505fc6676e4e544621c30862966e37dddb68e92efbe5d6b",
            signed.authorization.substringAfter("Signature="),
        )
    }

    @Test
    fun aDifferentBodyChangesTheSignature() {
        val first = signer.sign("POST", "example.amazonaws.com", "/", "{\"a\":1}", "20150830T123600Z")
        val second = signer.sign("POST", "example.amazonaws.com", "/", "{\"a\":2}", "20150830T123600Z")
        assertNotEquals(first.authorization, second.authorization)
    }

    @Test
    fun sessionTokenIsSigned() {
        val signed = signer.sign(
            method = "POST",
            host = "example.amazonaws.com",
            canonicalUri = "/",
            body = "{}",
            amzDate = "20150830T123600Z",
            contentType = "application/json",
            sessionToken = "session-token",
        )
        assertTrue(signed.signedHeaders.contains("x-amz-security-token"))
        assertTrue(signed.canonicalRequest.contains("x-amz-security-token:session-token"))
    }

    @Test
    fun encodesTheModelIdOnceForTheConversePath() {
        val model = "anthropic.claude-3-haiku-20240307-v1:0"
        val path = encodePath("/model/$model/converse")
        assertEquals("/model/anthropic.claude-3-haiku-20240307-v1%3A0/converse", path)
        val url = converseUrl("us-east-1", model).toHttpUrl()
        assertEquals(listOf("model", model, "converse"), url.pathSegments)
        assertFalse(url.encodedPath.contains("%25"))
    }
}
