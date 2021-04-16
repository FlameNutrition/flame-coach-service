package com.coach.flame.customer.security

import java.security.SecureRandom
import java.util.*

/**
 * Generate a Salt for hashing password
 *
 * Credits: https://dev.to/awwsmm/how-to-encrypt-a-password-in-java-42dh
 */
class Salt(private val length: Int) {

    init {
        check(length > 1) { "length must be > 1" }
    }

    fun generate(): String {

        val salt = ByteArray(length)
        SecureRandom.getInstanceStrong().nextBytes(salt)

        return Base64.getEncoder().encodeToString(salt)

    }

}
