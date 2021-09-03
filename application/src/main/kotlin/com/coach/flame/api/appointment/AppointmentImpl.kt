package com.coach.flame.api.appointment

import com.coach.flame.api.APIWrapperException
import com.coach.flame.api.appointment.request.AppointmentRequest
import com.coach.flame.api.appointment.response.Appointment
import com.coach.flame.api.appointment.response.AppointmentResponse
import com.coach.flame.api.appointment.response.Client
import com.coach.flame.appointment.AppointmentService
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.date.DateHelper
import com.coach.flame.date.DateHelper.toISODate
import com.coach.flame.date.DateHelper.toISODateWithOffset
import com.coach.flame.date.DateHelper.toZonedDateTime
import com.coach.flame.domain.AppointmentDto
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.IncomeDto
import com.coach.flame.domain.maker.AppointmentDtoMaker
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/appointment")
class AppointmentImpl(
    private val appointmentService: AppointmentService,
) : AppointmentAPI {

    companion object {
        private val toClient: (ClientDto) -> (Client) = {
            Client(
                identifier = it.identifier.toString(),
                firstName = it.firstName,
                lastName = it.lastName,
            )
        }

        private val toAppointment: (AppointmentDto) -> (Appointment) = {
            Appointment(
                identifier = it.identifier.toString(),
                dttmStarts = toISODateWithOffset(it.dttmStarts),
                dttmEnds = toISODateWithOffset(it.dttmEnds),
                price = it.income.price,
                notes = it.notes,
                client = toClient(it.safeClient)
            )
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/client/get")
    @ResponseBody
    override fun getAppointmentsClient(@RequestParam clientIdentifier: UUID): AppointmentResponse {
        return APIWrapperException.executeRequest {

            val appointments = appointmentService.getAllClientAppointments(clientIdentifier)

            AppointmentResponse(appointments = appointments.map { toAppointment(it) }.toSet())
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/coach/get")
    @ResponseBody
    override fun getAppointmentsCoach(@RequestParam coachIdentifier: UUID): AppointmentResponse {
        return APIWrapperException.executeRequest {

            val appointments = appointmentService.getAllCoachAppointments(coachIdentifier)

            AppointmentResponse(appointments = appointments.map { toAppointment(it) }.toSet())

        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/get")
    @ResponseBody
    override fun getAppointments(
        @RequestParam coachIdentifier: UUID,
        @RequestParam clientIdentifier: UUID,
    ): AppointmentResponse {
        return APIWrapperException.executeRequest {

            val appointments = appointmentService.getAppointments(coachIdentifier, clientIdentifier)

            AppointmentResponse(appointments = appointments.map { toAppointment(it) }.toSet())
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/create")
    @ResponseBody
    override fun createAppointment(
        @RequestParam coachIdentifier: UUID,
        @RequestParam clientIdentifier: UUID,
        @RequestBody(required = true) appointmentRequest: AppointmentRequest,
    ): AppointmentResponse {
        return APIWrapperException.executeRequest {

            val appointmentToPersist = AppointmentDto(
                identifier = UUID.randomUUID(),
                dttmStarts = toZonedDateTime(appointmentRequest.dttmStarts),
                dttmEnds = toZonedDateTime(appointmentRequest.dttmEnds),
                notes = appointmentRequest.notes,
                income = IncomeDto(appointmentRequest.price, IncomeDto.IncomeStatus.PENDING),
                delete = false)

            val appointment =
                appointmentService.createAppointment(coachIdentifier, clientIdentifier, appointmentToPersist)

            AppointmentResponse(appointments = setOf(toAppointment(appointment)))
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/update")
    @ResponseBody
    override fun updateAppointment(
        @RequestParam appointmentIdentifier: UUID,
        @RequestBody(required = true) appointmentRequest: AppointmentRequest,
    ): AppointmentResponse {
        return APIWrapperException.executeRequest {

            val appointmentToUpdate = AppointmentDto(
                identifier = appointmentIdentifier,
                dttmStarts = toZonedDateTime(appointmentRequest.dttmStarts),
                dttmEnds = toZonedDateTime(appointmentRequest.dttmEnds),
                notes = appointmentRequest.notes,
                income = IncomeDto(appointmentRequest.price, IncomeDto.IncomeStatus.PENDING))

            val appointment = appointmentService.updateAppointment(appointmentToUpdate)

            AppointmentResponse(appointments = setOf(toAppointment(appointment)))
        }
    }

    @LoggingRequest
    @LoggingResponse
    @DeleteMapping("/delete")
    @ResponseBody
    override fun deleteAppointment(@RequestParam appointmentIdentifier: UUID): AppointmentResponse {
        return APIWrapperException.executeRequest {

            appointmentService.deleteAppointment(appointmentIdentifier)

            AppointmentResponse(
                appointments = setOf(
                    Appointment(
                        identifier = appointmentIdentifier.toString()
                    ))
            )
        }
    }


}
