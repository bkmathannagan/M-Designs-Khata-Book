from scripts.file_util import save as w

w('app/src/test/java/com/mdesigns/khata/KhataBalanceCalculatorTest.kt', '''
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
''')

w('app/src/test/java/com/mdesigns/khata/CurrencyFormatterTest.kt', '''
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
''')

w('app/src/test/java/com/mdesigns/khata/CryptoUtilsTest.kt', '''
package com.mdesigns.khata

import com.mdesigns.khata.core.security.CryptoUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CryptoUtilsTest {

    @Test
    fun testPinHashVerification() {
        val pin = "1234"
        val hash = CryptoUtils.hashPin(pin)

        assertTrue(CryptoUtils.verifyPin("1234", hash))
        assertFalse(CryptoUtils.verifyPin("0000", hash))
        assertFalse(CryptoUtils.verifyPin("1235", hash))
    }
}
''')
print("Unit tests generated")
