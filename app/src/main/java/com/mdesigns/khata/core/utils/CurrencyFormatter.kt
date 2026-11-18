package com.mdesigns.khata.core.utils

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

object CurrencyFormatter {
    private val indianLocale = Locale("en", "IN")
    private val formatter = NumberFormat.getCurrencyInstance(indianLocale).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    fun format(amount: Double, includeSymbol: Boolean = true): String {
        val formatted = formatter.format(abs(amount))
        return if (includeSymbol) {
            formatted
        } else {
            formatted.replace("₹", "").trim()
        }
    }

    fun formatWithSign(amount: Double): String {
        val formatted = format(abs(amount), true)
        return when {
            amount > 0 -> "+$formatted"
            amount < 0 -> "-$formatted"
            else -> formatted
        }
    }
}
