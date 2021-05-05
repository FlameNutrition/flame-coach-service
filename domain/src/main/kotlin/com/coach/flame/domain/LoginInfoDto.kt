package com.coach.flame.domain

import java.time.LocalDateTime
import java.util.*

data class LoginInfoDto(
    val userId: Long? = null,
    val sessionId: Long? = null,
    val username: String,
    val password: String,
    val keyDecrypt: String?,
    val expirationDate: LocalDateTime? = null,
    val token: UUID? = null,
) {
    override fun toString(): String {
        return "LoginInfoDto(" +
                "username='$username', " +
                "password='*****', " +
                "keyDecrypt=$keyDecrypt, " +
                "expirationDate=$expirationDate, " +
                "token=$token" +
                ")"
    }
}
