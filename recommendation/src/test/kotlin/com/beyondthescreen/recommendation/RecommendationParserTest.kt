package com.beyondthescreen.recommendation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationParserTest {
    private val good = """
        {"categories":[
          {"id":"books","label":"Books","suggestions":[
            {"name":"Sextant","why":"They still navigate by hand.","searchQuery":"brass sextant"},
            {"name":"Logbook","why":"The watch is written down.","searchQuery":"ship logbook"}
          ]},
          {"id":"models","label":"Model ships","suggestions":[
            {"name":"Frigate kit","why":"The hull is the main character.","searchQuery":"wooden frigate model kit"}
          ]}
        ]}
    """.trimIndent()

    @Test
    fun parsesABareObject() {
        val parsed = RecommendationParser.parse(good, "master-and-commander") as ParseOutcome.Ok
        assertEquals(Source.BEDROCK, parsed.set.source)
        assertEquals(2, parsed.set.categories.size)
        assertEquals("https://www.amazon.com/s?k=brass%20sextant", parsed.set.categories[0].suggestions[0].url)
    }

    @Test
    fun parsesFencedJsonWithProse() {
        val fenced = "Here you go:\n```json\n$good\n```\nEnjoy."
        val parsed = RecommendationParser.parse(fenced, "master-and-commander")
        assertTrue(parsed is ParseOutcome.Ok)
    }

    @Test
    fun rejectsGarbageAndThinResults() {
        assertTrue(RecommendationParser.parse("not json", "x") is ParseOutcome.Failed)
        val thin = """{"categories":[{"id":"books","label":"Books","suggestions":[{"name":"A","why":"Too short.","searchQuery":"C"}]}]}"""
        assertTrue(RecommendationParser.parse(thin, "x") is ParseOutcome.Failed)
    }

    @Test
    fun dropsStreamingCategoriesAndAffiliateQueries() {
        val text = """
            {"categories":[
              {"id":"movies","label":"Movies","suggestions":[
                {"name":"More films","why":"People who watched this also watched these.","searchQuery":"movies like it"}
              ]},
              {"id":"books","label":"Books","suggestions":[
                {"name":"Sextant","why":"They still navigate by hand.","searchQuery":"brass sextant&tag=hack-20"}
              ]},
              {"id":"tools","label":"Tools","suggestions":[
                {"name":"Binoculars","why":"Someone is always on the horizon.","searchQuery":"marine binoculars"}
              ]}
            ]}
        """.trimIndent()
        val parsed = RecommendationParser.parse(text, "master-and-commander") as ParseOutcome.Ok
        assertEquals(listOf("books", "tools"), parsed.set.categories.map { it.id })
        assertEquals("brass sextant", parsed.set.categories[0].suggestions[0].searchQuery)
        assertTrue(parsed.set.categories.none { it.label.equals("Movies", ignoreCase = true) })
    }
}
