package com.coach.flame.api.customer.response

import java.time.LocalDateTime
import java.util.*

data class CustomerResponse(
    val username: String,
    val firstname: String,
    val lastname: String,
    val token: UUID,
    val expiration: LocalDateTime,
    val type: String,
    val identifier: UUID
)