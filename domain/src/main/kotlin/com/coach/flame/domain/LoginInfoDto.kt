package com.coach.flame.domain

import java.time.LocalDateTime
import java.util.*

data class LoginInfoDto(
    val username: String?,
    val password: String?,
    val expirationDate: LocalDateTime? = null,
    val token: UUID? = null
)