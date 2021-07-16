package com.coach.flame.api.appointment.request

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AppointmentRequest(
    val date: String,
    val price: Float,
    val notes: String? = null,
)
