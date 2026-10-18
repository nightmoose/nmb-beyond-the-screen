package com.beyondthescreen.recommendation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI

class CatalogAndFallbackTest {
    @Test
    fun catalogHasTenDistinctTitlesAndFourPlayableFilms() {
        assertEquals(10, Catalog.all.size)
        assertEquals(Catalog.all.size, Catalog.all.map { it.id }.toSet().size)
        assertEquals(Catalog.curatedMatches(), FallbackCatalog.curatedIds)
        val playable = Catalog.all.filter { it.playable }
        assertEquals(4, playable.size)
        playable.forEach { title ->
            val url = title.playbackUrl!!
            assertTrue(url.startsWith("https://") || url.startsWith("bundled://"))
            val credit = title.attribution!!
            assertTrue(credit.license.contains("CC BY"))
            assertTrue(credit.credit.isNotBlank())
            assertTrue(credit.sourceUrl.startsWith("https://"))
        }
        assertEquals(Catalog.BUNDLED_SINTEL, Catalog.byId("sintel").playbackUrl)
    }

    @Test
    fun everyCuratedTitleIsStableAndShopable() {
        Catalog.all.forEach { title ->
            val first = FallbackCatalog.forTitle(title)
            val second = FallbackCatalog.forTitle(title)
            assertEquals(first, second)
            assertEquals(Source.CURATED, first.source)
            assertTrue(first.categories.size >= 3)
            first.categories.forEach { category ->
                assertTrue(category.suggestions.size >= 2)
                category.suggestions.forEach { suggestion ->
                    assertTrue(suggestion.why.length in 20..160)
                    assertFalse(suggestion.why.contains('\n'))
                    val uri = URI(suggestion.url)
                    assertEquals("www.amazon.com", uri.host)
                    assertEquals("/s", uri.path)
                    assertTrue(uri.rawQuery.startsWith("k="))
                    assertFalse("affiliate tag in ${suggestion.url}", uri.rawQuery.contains("tag="))
                    assertEquals(1, uri.rawQuery.split("&").size)
                }
            }
        }
    }

    @Test
    fun masterAndCommanderBridgesOutOfStreaming() {
        val set = FallbackCatalog.forTitle(Catalog.byId("master-and-commander"))
        assertTrue(set.categories.any { it.id == "books" })
        val queries = set.categories.flatMap { it.suggestions }.joinToString(" ") { it.searchQuery.lowercase() }
        assertTrue(queries.contains("sextant"))
        assertTrue(queries.contains("model"))
        assertTrue(queries.contains("pea coat"))
        assertFalse(queries.contains("prime video"))
    }

    @Test
    fun unknownTitlesStillReturnADeterministicSet() {
        val ghost = Catalog.byId("sintel").copy(id = "not-a-real-title", name = "Ghost", playbackUrl = null)
        val first = FallbackCatalog.forTitle(ghost)
        val second = FallbackCatalog.forTitle(ghost)
        assertEquals(first, second)
        assertTrue(first.categories.size >= 2)
        assertEquals("not-a-real-title", first.titleId)
    }

    private fun Catalog.curatedMatches(): Set<String> = all.map { it.id }.toSet()
}
