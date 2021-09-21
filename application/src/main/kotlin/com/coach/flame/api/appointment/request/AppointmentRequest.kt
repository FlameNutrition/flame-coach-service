package com.coach.flame.api.appointment.request

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AppointmentRequest(
    val dttmStarts: String,
    val dttmEnds: String,
    val price: Float,
    val notes: String? = null,
    val incomeStatus: String = "PENDING",
)
