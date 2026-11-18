package com.mdesigns.khata

import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class KhataBalanceCalculatorTest {

    @Test
    fun testReceivableOpeningBalancePlusDebitMinusCredit() {
        // Opening balance: 5,000 You Collect (Debit = true)
        // Debit (You Give): 2,000
        // Credit (You Receive): 1,000
        // Expected Net: 5000 + 2000 - 1000 = 6,000 (You Collect)
        val result = KhataBalanceCalculator.calculate(
            openingBalance = 5000.0,
            isOpeningBalanceDebit = true,
            totalDebit = 2000.0,
            totalCredit = 1000.0
        )

        assertEquals(6000.0, result.netBalance, 0.001)
        assertEquals(KhataBalanceCalculator.BalanceStatus.YOU_COLLECT, result.status)
        assertEquals(6000.0, result.displayAmount, 0.001)
    }

    @Test
    fun testPayableOpeningBalanceSettlement() {
        // Opening balance: 3,000 You Give (Debit = false)
        // Debit (You Give): 1,000
        // Credit (You Receive): 4,000
        // Expected Net: -3000 + 1000 - 4000 = -6,000 (You Give)
        val result = KhataBalanceCalculator.calculate(
            openingBalance = 3000.0,
            isOpeningBalanceDebit = false,
            totalDebit = 1000.0,
            totalCredit = 4000.0
        )

        assertEquals(-6000.0, result.netBalance, 0.001)
        assertEquals(KhataBalanceCalculator.BalanceStatus.YOU_GIVE, result.status)
        assertEquals(6000.0, result.displayAmount, 0.001)
    }

    @Test
    fun testSettledAccount() {
        val result = KhataBalanceCalculator.calculate(
            openingBalance = 1000.0,
            isOpeningBalanceDebit = true,
            totalDebit = 2000.0,
            totalCredit = 3000.0
        )

        assertEquals(0.0, result.netBalance, 0.001)
        assertEquals(KhataBalanceCalculator.BalanceStatus.SETTLED, result.status)
    }
}
