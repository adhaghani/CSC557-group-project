package com.example.groupproject.util

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordUtils {

    private const val ALGORITHM = "SHA-256"
    private const val SALT_BYTES = 32

    fun generateSalt(): String {
        val salt = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(salt)
        return bytesToHex(salt)
    }

    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance(ALGORITHM)
        val saltedInput = salt + password
        val hashBytes = digest.digest(saltedInput.toByteArray(Charsets.UTF_8))
        return bytesToHex(hashBytes)
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val computedHash = hashPassword(password, salt)
        return MessageDigest.isEqual(
            computedHash.toByteArray(Charsets.UTF_8),
            expectedHash.toByteArray(Charsets.UTF_8)
        )
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
