from scripts.file_util import save as w

w('app/src/main/java/com/mdesigns/khata/core/utils/CurrencyFormatter.kt', '''
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
''')

w('app/src/main/java/com/mdesigns/khata/core/utils/DateUtils.kt', '''
package com.mdesigns.khata.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val displayTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val displayDateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatDate(timestamp: Long): String = displayDateFormat.format(Date(timestamp))
    fun formatTime(timestamp: Long): String = displayTimeFormat.format(Date(timestamp))
    fun formatDateTime(timestamp: Long): String = displayDateTimeFormat.format(Date(timestamp))
    fun formatIsoDate(timestamp: Long): String = isoDateFormat.format(Date(timestamp))

    fun isToday(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }

    fun isThisMonth(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == target.get(Calendar.MONTH)
    }

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getStartOfMonth(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/core/utils/KhataBalanceCalculator.kt', '''
package com.mdesigns.khata.core.utils

object KhataBalanceCalculator {

    enum class BalanceStatus {
        YOU_COLLECT,
        YOU_GIVE,
        SETTLED
    }

    data class BalanceResult(
        val netBalance: Double,
        val totalDebit: Double,
        val totalCredit: Double,
        val status: BalanceStatus,
        val displayAmount: Double
    )

    fun calculate(
        openingBalance: Double,
        isOpeningBalanceDebit: Boolean,
        totalDebit: Double,
        totalCredit: Double
    ): BalanceResult {
        val effectiveOpening = if (isOpeningBalanceDebit) openingBalance else -openingBalance
        val net = effectiveOpening + totalDebit - totalCredit
        val status = when {
            net > 0.001 -> BalanceStatus.YOU_COLLECT
            net < -0.001 -> BalanceStatus.YOU_GIVE
            else -> BalanceStatus.SETTLED
        }
        return BalanceResult(
            netBalance = net,
            totalDebit = totalDebit,
            totalCredit = totalCredit,
            status = status,
            displayAmount = kotlin.math.abs(net)
        )
    }
}
''')

w('app/src/main/java/com/mdesigns/khata/core/utils/UpiHelper.kt', '''
package com.mdesigns.khata.core.utils

import android.net.Uri
import java.net.URLEncoder

object UpiHelper {
    fun createUpiUri(
        vpa: String,
        name: String,
        amount: Double,
        note: String = "Khata Settlement"
    ): Uri {
        val formattedAmount = String.format(java.util.Locale.US, "%.2f", amount)
        val encodedName = URLEncoder.encode(name, "UTF-8")
        val encodedNote = URLEncoder.encode(note, "UTF-8")
        val uriString = "upi://pay?pa=$vpa&pn=$encodedName&am=$formattedAmount&cu=INR&tn=$encodedNote"
        return Uri.parse(uriString)
    }
}
''')
print("Utils generated")
