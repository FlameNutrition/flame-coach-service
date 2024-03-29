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
import com.coach.flame.date.DateHelper.toISODateWithOffset
import com.coach.flame.date.DateHelper.toZonedDateTime
import com.coach.flame.domain.*
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
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
                client = toClient(it.safeClient),
                incomeStatus = it.income.status.name
            )
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/client/getNext")
    @ResponseBody
    override fun getNextClientAppointment(@RequestParam clientIdentifier: UUID): AppointmentResponse {
        return APIWrapperException.executeRequest {
            val appointment = appointmentService.getNextAppointment(clientIdentifier, CustomerTypeDto.CLIENT)

            AppointmentResponse(appointments = hashSetOf(toAppointment(appointment)))
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/coach/getNext")
    @ResponseBody
    override fun getNextCoachAppointment(@RequestParam coachIdentifier: UUID): AppointmentResponse {
        return APIWrapperException.executeRequest {
            val appointment = appointmentService.getNextAppointment(coachIdentifier, CustomerTypeDto.COACH)

            AppointmentResponse(appointments = hashSetOf(toAppointment(appointment)))
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/client/get")
    @ResponseBody
    override fun getAppointmentsClient(
        @RequestParam clientIdentifier: UUID,
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) to: String?,
    ): AppointmentResponse {
        return APIWrapperException.executeRequest {

            val interval = getDateIntervalDto(to, from)

            val appointments =
                appointmentService.getAllClientAppointments(clientIdentifier, interval)

            AppointmentResponse(appointments = appointments.map { toAppointment(it) }.toSet())
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/coach/get")
    @ResponseBody
    override fun getAppointmentsCoach(
        @RequestParam coachIdentifier: UUID,
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) to: String?,
    ): AppointmentResponse {
        return APIWrapperException.executeRequest {

            val interval = getDateIntervalDto(to, from)

            val appointments =
                appointmentService.getAllCoachAppointments(coachIdentifier, interval)

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
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) to: String?,
    ): AppointmentResponse {
        return APIWrapperException.executeRequest {

            val interval = getDateIntervalDto(to, from)

            val appointments =
                appointmentService.getAppointments(coachIdentifier, clientIdentifier, interval)

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
                delete = false
            )

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
                income = IncomeDto(
                    appointmentRequest.price,
                    IncomeDto.IncomeStatus.valueOf(appointmentRequest.incomeStatus)
                )
            )

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
                        identifier = appointmentIdentifier.toString(),
                        incomeStatus = null
                    )
                )
            )
        }
    }

    private fun getDateIntervalDto(to: String?, from: String?): Optional<DateIntervalDto> {
        val interval = to?.let {
            val actualFrom = if (from != null) {
                DateHelper.toDate(from)
            } else {
                LocalDate.now()
            }
            DateIntervalDto(actualFrom, DateHelper.toDate(to))
        }
        return Optional.ofNullable(interval)
    }

}
