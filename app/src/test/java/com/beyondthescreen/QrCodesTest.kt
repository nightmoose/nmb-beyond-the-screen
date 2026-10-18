package com.beyondthescreen

import com.beyondthescreen.ui.QrCodes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCodesTest {
    @Test
    fun searchUrlBecomesASquareCode() {
        val url = "https://www.amazon.com/s?k=brass%20sextant"
        val matrix = QrCodes.matrix(url)
        val again = QrCodes.matrix(url)
        assertTrue(matrix.width > 20)
        assertEquals(matrix.width, matrix.height)
        assertEquals(matrix.get(8, 8), again.get(8, 8))
    }
}
