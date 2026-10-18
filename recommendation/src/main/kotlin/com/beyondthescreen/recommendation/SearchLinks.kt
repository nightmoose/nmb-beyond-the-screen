package com.beyondthescreen.recommendation

import java.net.URLDecoder
import java.net.URLEncoder

object SearchLinks {
    fun sanitizeQuery(raw: String): String {
        var query = raw.trim()
        if (query.startsWith("http://") || query.startsWith("https://")) {
            val encoded = K_PARAM.find(query)?.groupValues?.get(1)
            if (encoded != null) {
                query = URLDecoder.decode(encoded, "UTF-8")
            }
        }
        query = query.replace(TAG_PARAM, " ")
        query = query.replace(WHITESPACE, " ").trim().trim('"').trim('\'')
        return query.take(MAX_QUERY)
    }

    fun urlFor(rawQuery: String): String {
        val query = sanitizeQuery(rawQuery)
        val encoded = URLEncoder.encode(query, "UTF-8").replace("+", "%20")
        return "$AMAZON_SEARCH$encoded"
    }

    private const val AMAZON_SEARCH = "https://www.amazon.com/s?k="
    private const val MAX_QUERY = 120
    private val K_PARAM = Regex("[?&]k=([^&]+)")
    private val TAG_PARAM = Regex("(?i)[&?]?\\btag=[^\\s&]*")
    private val WHITESPACE = Regex("[\\r\\n\\t ]+")
}
