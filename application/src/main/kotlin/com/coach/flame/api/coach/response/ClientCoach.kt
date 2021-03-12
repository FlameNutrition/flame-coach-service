package com.coach.flame.api.coach.response

import java.util.*

data class ClientCoach(
    val firstname: String,
    val lastname: String,
    val identifier: UUID
)