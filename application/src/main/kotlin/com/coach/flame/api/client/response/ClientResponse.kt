package com.coach.flame.api.client.response

import java.time.LocalDateTime
import java.util.*

data class ClientResponse(
    val username: String,
    val firstname: String,
    val lastname: String,
    val token: UUID,
    val expiration: LocalDateTime
)