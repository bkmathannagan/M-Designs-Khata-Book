package com.mdesigns.khata

import com.mdesigns.khata.core.utils.CurrencyFormatter
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun testIndianRupeeFormat() {
        val formatted = CurrencyFormatter.format(50000.0)
        assertTrue(formatted.contains("50,000"))
    }

    @Test
    fun testFormatWithSign() {
        val positive = CurrencyFormatter.formatWithSign(1500.0)
        assertTrue(positive.startsWith("+"))

        val negative = CurrencyFormatter.formatWithSign(-1500.0)
        assertTrue(negative.startsWith("-"))
    }
}
