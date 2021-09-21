package com.coach.flame.api.appointment.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Appointment(
    val identifier: String,
    val dttmStarts: String? = null,
    val dttmEnds: String? = null,
    val price: Float? = null,
    val notes: String? = null,
    val client: Client? = null,
    val incomeStatus: String?
)
