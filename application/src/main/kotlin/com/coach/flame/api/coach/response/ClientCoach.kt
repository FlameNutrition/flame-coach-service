package com.coach.flame.api.coach.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ClientCoach(
    val identifier: UUID,
    val firstname: String,
    val lastname: String,
    val status: String?,
    val email: String,
    val registrationDate: LocalDate
)