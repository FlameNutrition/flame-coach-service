package com.coach.flame.customer.security

import com.coach.flame.customer.SecurityException
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Generate an hash password
 *
 * Credits: https://dev.to/awwsmm/how-to-encrypt-a-password-in-java-42dh
 *
 * @param algorithm type you want to use for the hashing password
 * @param iterations how many times you want to perform the hashing algorithm
 * @param lengthKey desired length of the resulting cryptographic key, in bits
 */
class HashPassword(
    private val algorithm: String,
    private val iterations: Int,
    private val lengthKey: Int,
) {

    init {
        check(algorithm.isNotBlank()) { "algorithm can not be empty" }
        check(iterations > 0) { "iterations must be > 0" }
        check(lengthKey > 0) { "lengthKey must be > 0" }
    }

    /**
     * Generate the hash password
     *
     * @param password password to hash
     * @param salt the salt for hashing the password, please use the [Salt.generate] method
     *
     * @return the hash password
     */
    fun generate(password: String, salt: String): String {

        val chars = password.toCharArray()
        val bytes = salt.toByteArray()

        //Define how generate the hashed password
        val spec = PBEKeySpec(chars, bytes, iterations, lengthKey)

        //Clean the chars array
        Arrays.fill(chars, Char.MIN_VALUE)

        try {

            val secretKeyFactory = SecretKeyFactory.getInstance(algorithm)
            val securePassword = secretKeyFactory.generateSecret(spec).encoded

            return Base64.getEncoder().encodeToString(securePassword)

        } catch (ex: Exception) {
            throw SecurityException("Something happened when trying to encrypt the password.", ex)
        } finally {
            spec.clearPassword()
        }

    }

    /**
     * Verify if plain password text is generate the same hash password
     *
     * @param password plain text password
     * @param hashPassword hash password generated with salt
     * @param salt the same salt used for the hash password
     *
     * @return true if password has the same hash of [hashPassword], otherwise false
     */
    fun verify(password: String, hashPassword: String, salt: String): Boolean {
        val newHashPassword = generate(password, salt)

        return newHashPassword == hashPassword

    }

}
