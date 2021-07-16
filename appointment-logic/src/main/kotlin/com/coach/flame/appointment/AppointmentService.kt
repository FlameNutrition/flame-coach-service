package com.coach.flame.appointment

import com.coach.flame.domain.AppointmentDto
import java.util.*

interface AppointmentService {

    fun createAppointment(coachIdentifier: UUID, clientIdentifier: UUID, appointmentDto: AppointmentDto): AppointmentDto

    fun getAllCoachAppointments(coachIdentifier: UUID): List<AppointmentDto>

    fun getAllClientAppointments(clientIdentifier: UUID): List<AppointmentDto>

    fun getAppointments(coachIdentifier: UUID, clientIdentifier: UUID): List<AppointmentDto>

    fun updateAppointment(appointmentDto: AppointmentDto): AppointmentDto

    fun deleteAppointment(identifier: UUID)

}
