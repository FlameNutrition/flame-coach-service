package com.coach.flame.api.client.response

import java.util.*

data class EnrollmentResponse(
    val client: UUID,
    val status: String,
    val coach: Coach?,
)
