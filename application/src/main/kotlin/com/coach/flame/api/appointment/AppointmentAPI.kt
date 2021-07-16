package com.coach.flame.api.appointment

import com.coach.flame.api.appointment.request.AppointmentRequest
import com.coach.flame.api.appointment.response.AppointmentResponse
import java.util.*

interface AppointmentAPI {

    fun getAppointmentsClient(clientIdentifier: UUID): AppointmentResponse

    fun getAppointmentsCoach(coachIdentifier: UUID): AppointmentResponse

    fun getAppointments(coachIdentifier: UUID, clientIdentifier: UUID): AppointmentResponse

    fun createAppointment(
        coachIdentifier: UUID,
        clientIdentifier: UUID,
        appointmentRequest: AppointmentRequest,
    ): AppointmentResponse

    fun updateAppointment(appointmentIdentifier: UUID, appointmentRequest: AppointmentRequest): AppointmentResponse

    fun deleteAppointment(appointmentIdentifier: UUID): AppointmentResponse

}
