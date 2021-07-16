package com.coach.flame.api.appointment.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Client(
    val identifier: String,
    val firstName: String,
    val lastName: String,
)
