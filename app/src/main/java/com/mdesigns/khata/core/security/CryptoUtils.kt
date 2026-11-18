package com.mdesigns.khata.core.security

import java.security.MessageDigest

object CryptoUtils {
    private const val SALT = "M_DESIGNS_KHATA_SECURE_SALT_2026"

    fun hashPin(pin: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((pin + SALT).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPin(inputPin: String, storedHash: String): Boolean {
        return hashPin(inputPin) == storedHash
    }
}
