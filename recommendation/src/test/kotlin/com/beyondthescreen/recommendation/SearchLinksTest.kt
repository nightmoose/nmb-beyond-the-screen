package com.beyondthescreen.recommendation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI

class SearchLinksTest {
    @Test
    fun encodesAPlainSearch() {
        assertEquals(
            "https://www.amazon.com/s?k=brass%20sextant",
            SearchLinks.urlFor("brass sextant"),
        )
    }

    @Test
    fun encodesAmpersandsInsideTheKeyword() {
        val url = SearchLinks.urlFor("rock & roll poster")
        val uri = URI(url)
        assertEquals("www.amazon.com", uri.host)
        assertEquals("/s", uri.path)
        assertTrue(uri.rawQuery.startsWith("k="))
        assertFalse(uri.rawQuery.contains("tag="))
        assertTrue(uri.rawQuery.contains("%26"))
    }

    @Test
    fun stripsAffiliateTagsFromATypedQuery() {
        assertEquals("brass sextant", SearchLinks.sanitizeQuery("brass sextant&tag=hack-20"))
        val uri = URI(SearchLinks.urlFor("brass sextant&tag=hack-20"))
        assertEquals("k=brass%20sextant", uri.rawQuery)
    }

    @Test
    fun extractsTheKeywordFromAFullSearchUrl() {
        val cleaned = SearchLinks.sanitizeQuery("https://www.amazon.com/s?k=brass%20sextant&tag=hack-20")
        assertEquals("brass sextant", cleaned)
        assertFalse(SearchLinks.urlFor(cleaned).contains("tag="))
    }
}
