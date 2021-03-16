package com.coach.flame.api.client.request

import java.util.*

data class EnrollmentRequest(
    val client: UUID?,
    val coach: UUID?,
    val accept: Boolean?,
)