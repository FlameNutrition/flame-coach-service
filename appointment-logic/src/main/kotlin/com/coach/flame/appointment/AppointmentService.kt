package com.coach.flame.appointment

import com.coach.flame.domain.AppointmentDto
import com.coach.flame.domain.DateIntervalDto
import java.util.*

interface AppointmentService {

    fun createAppointment(coachIdentifier: UUID, clientIdentifier: UUID, appointmentDto: AppointmentDto): AppointmentDto

    fun getAllCoachAppointments(coachIdentifier: UUID, intervalFilter: Optional<DateIntervalDto>): List<AppointmentDto>

    fun getAllClientAppointments(clientIdentifier: UUID, intervalFilter: Optional<DateIntervalDto>): List<AppointmentDto>

    fun getAppointments(coachIdentifier: UUID, clientIdentifier: UUID, intervalFilter: Optional<DateIntervalDto>): List<AppointmentDto>

    fun updateAppointment(appointmentDto: AppointmentDto): AppointmentDto

    fun deleteAppointment(identifier: UUID)

}
