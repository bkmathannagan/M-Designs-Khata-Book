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
