package com.coach.flame.api.appointment.response

data class AppointmentResponse(
    val appointments: Set<Appointment> = setOf(),
)
