package com.coach.flame.appointment

import com.coach.flame.date.DateHelper.toAnotherZone
import com.coach.flame.domain.AppointmentDto
import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.Appointment.Companion.toAppointment
import com.coach.flame.jpa.repository.AppointmentRepository
import com.coach.flame.jpa.repository.operations.ClientRepositoryOperation
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.util.*

@Service
class AppointmentServiceImpl(
    private val coachOperations: CoachRepositoryOperation,
    private val clientOperations: ClientRepositoryOperation,
    private val appointmentRepository: AppointmentRepository,
) : AppointmentService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(AppointmentServiceImpl::class.java)
    }

    @Transactional
    override fun createAppointment(
        coachIdentifier: UUID, clientIdentifier: UUID,
        appointmentDto: AppointmentDto,
    ): AppointmentDto {

        LOGGER.info("opr='createAppointment', msg='Create a new appointment'")

        val coach = coachOperations.getCoach(coachIdentifier)
        val client = coach.clients.firstOrNull { it.uuid == clientIdentifier }
            ?: throw CustomerNotFoundException("Could not find any client with uuid: $clientIdentifier.")

        val appointment = appointmentDto
            .apply {
                this.coach = coach.toDto()
                this.client = client.toDto()
            }
            .toAppointment()

        LOGGER.debug("opr='createAppointment', msg='Appointment to persist', appointment={}", appointment)

        val appointmentPersisted = appointmentRepository.save(appointment).toDto()

        LOGGER.info("opr='createAppointment', msg='Appointment created with success', appointment={}",
            appointmentPersisted)

        return appointmentPersisted

    }

    @Transactional(readOnly = true)
    override fun getAllCoachAppointments(
        coachIdentifier: UUID,
        intervalFilter: Optional<DateIntervalDto>,
    ): List<AppointmentDto> {

        LOGGER.info("opr='getAllCoachAppointments', msg='Get coach appointments', coachIdentifier={}, intervalFilter={}",
            coachIdentifier, intervalFilter)

        val coach = coachOperations.getCoach(coachIdentifier)

        return if (intervalFilter.isPresent) {
            val interval = intervalFilter.get()

            appointmentRepository
                .findAppointmentsByCoachBetweenDates(coach.uuid,
                    interval.from.atStartOfDay(),
                    interval.to.atStartOfDay())
                .map { it.toDto() }
        } else {
            appointmentRepository.findAppointmentsByCoach(coach.uuid)
                .map { it.toDto() }
        }
    }

    @Transactional(readOnly = true)
    override fun getAllClientAppointments(
        clientIdentifier: UUID,
        intervalFilter: Optional<DateIntervalDto>,
    ): List<AppointmentDto> {

        LOGGER.info("opr='getAllClientAppointments', msg='Get client appointments', clientIdentifier={}, intervalFilter={}",
            clientIdentifier, intervalFilter)

        val client = clientOperations.getClient(clientIdentifier)

        return if (intervalFilter.isPresent) {
            val interval = intervalFilter.get()

            appointmentRepository
                .findAppointmentsByClientBetweenDates(client.uuid,
                    interval.from.atStartOfDay(),
                    interval.to.atStartOfDay())
                .map { it.toDto() }
        } else {
            appointmentRepository.findAppointmentsByClient(client.uuid)
                .map { it.toDto() }
        }

    }

    @Transactional(readOnly = true)
    override fun getAppointments(
        coachIdentifier: UUID,
        clientIdentifier: UUID,
        intervalFilter: Optional<DateIntervalDto>,
    ): List<AppointmentDto> {

        LOGGER.info("opr='getAppointments', msg='Get appointments', coachIdentifier={}, clientIdentifier={}, intervalFilter={}",
            coachIdentifier,
            clientIdentifier,
            intervalFilter)

        val coach = coachOperations.getCoach(coachIdentifier)
        val client = coach.clients.firstOrNull { it.uuid == clientIdentifier }
            ?: throw CustomerNotFoundException("Could not find any client with uuid: $clientIdentifier.")

        return if (intervalFilter.isPresent) {
            val interval = intervalFilter.get()
            appointmentRepository
                .findAppointmentsBetweenDates(coach.uuid, client.uuid,
                    interval.from.atStartOfDay(),
                    interval.to.atStartOfDay())
                .map { it.toDto() }
        } else {
            appointmentRepository.findAppointments(coach.uuid, client.uuid)
                .map { it.toDto() }
        }
    }

    @Transactional
    override fun updateAppointment(appointmentDto: AppointmentDto): AppointmentDto {

        LOGGER.info("opr='updateAppointment', msg='Update appointments'")

        val appointmentEntity = appointmentRepository.findByUuidAndDeleteFalse(appointmentDto.identifier) ?: run {
            LOGGER.warn("opr='updateAppointment', msg='Appointment doesn't exist', appointment={}",
                appointmentDto.identifier)
            throw AppointmentNotFoundException("Appointment not found, please check the identifier.")
        }

        val appointmentToPersist = appointmentEntity
            .apply {
                dttmStarts = toAnotherZone(appointmentDto.dttmStarts, ZoneId.systemDefault()).toLocalDateTime()
                dttmEnds = toAnotherZone(appointmentDto.dttmEnds, ZoneId.systemDefault()).toLocalDateTime()
                income.price = appointmentDto.income.price
                income.status = appointmentDto.income.status.name
                currency = appointmentDto.currency.currencyCode
                notes = appointmentDto.notes
            }

        LOGGER.debug("opr='updateAppointment', msg='Appointment to persist', appointment={}", appointmentToPersist)

        val appointmentPersisted = appointmentRepository.save(appointmentToPersist)
            .toDto()

        LOGGER.info("opr='updateAppointment', msg='Appointment updated with success', appointment={}",
            appointmentPersisted)

        return appointmentPersisted

    }

    @Transactional
    override fun deleteAppointment(identifier: UUID) {

        LOGGER.info("opr='deleteAppointment', msg='Delete appointment', appointment={}", identifier)

        val appointmentEntity = appointmentRepository.findByUuidAndDeleteFalse(identifier) ?: run {
            LOGGER.warn("opr='updateAppointment', msg='Appointment doesn't exist', appointment={}", identifier)
            throw AppointmentMissingDeleteException("Didn't find the following uuid appointment: $identifier")
        }

        val appointmentPersisted = appointmentRepository.save(appointmentEntity.apply {
            delete = true
        })

        LOGGER.info("opr='updateAppointment', msg='Appointment marked as deleted with success', appointment={}",
            appointmentPersisted)
    }
}
