package com.coach.flame.base64

import java.util.Base64

object Base64 {

    private val encoded = Base64.getEncoder()
    private val decoded = Base64.getDecoder()

    /**
     * Encode string using [Base64]
     *
     * @param value string value
     *
     * @return value encoded using base64
     */
    fun encode(value: String): String {
        return encoded.encodeToString(value.toByteArray())
    }

    /**
     * Decode string using [Base64]
     *
     * @param value string value encoded using base64
     *
     * @return value decoded using base64
     */
    fun decode(value: String): String {
        val byteArray = decoded.decode(value)
        return String(byteArray)
    }

}
