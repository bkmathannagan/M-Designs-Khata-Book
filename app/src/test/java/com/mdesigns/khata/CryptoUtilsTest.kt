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
