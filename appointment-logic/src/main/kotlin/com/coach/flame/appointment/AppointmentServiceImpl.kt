package com.coach.flame.appointment

import com.coach.flame.date.DateHelper.toAnotherZone
import com.coach.flame.date.DateHelper.toISODateWithOffset
import com.coach.flame.date.DateHelper.toZonedDateTime
import com.coach.flame.domain.AppointmentDto
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

        checkNotNull(appointmentDto.dttmTxt) { "dttmTxt can not be null" }

        LOGGER.info("opr='createAppointment', msg='Create a new appointment'")

        val coach = coachOperations.getCoach(coachIdentifier)
        val client = coach.clients.firstOrNull { it.uuid == clientIdentifier }
            ?: throw CustomerNotFoundException("Could not find any client with uuid: $clientIdentifier.")

        val zonedDateTime = toZonedDateTime(appointmentDto.dttmTxt!!)
        val dttmZonedSystem = toAnotherZone(zonedDateTime, ZoneId.systemDefault()).toLocalDateTime()

        LOGGER.debug("opr='createAppointment', msg='Convert date string to date with timezone', " +
                "dateToConvert={}, date={}, sysDate={}", appointmentDto.dttmTxt, zonedDateTime, dttmZonedSystem)

        val appointment = appointmentDto
            .apply {
                this.dttm = dttmZonedSystem
                this.coach = coach.toDto()
                this.client = client.toDto()
            }
            .toAppointment()

        LOGGER.debug("opr='createAppointment', msg='Appointment to persist', appointment={}", appointment)

        val appointmentPersisted = appointmentRepository.save(appointment).toDto()
            .apply {
                this.dttmTxt = appointmentDto.dttmTxt
                this.dttmZoned = zonedDateTime
            }

        LOGGER.info("opr='createAppointment', msg='Appointment created with success', appointment={}",
            appointmentPersisted)

        return appointmentPersisted

    }

    @Transactional(readOnly = true)
    override fun getAllCoachAppointments(coachIdentifier: UUID): List<AppointmentDto> {

        LOGGER.info("opr='getAllCoachAppointments', msg='Get coach appointments', coachIdentifier={}",
            coachIdentifier)

        val coach = coachOperations.getCoach(coachIdentifier)

        return appointmentRepository.findAppointmentsByCoach(coach.uuid)
            .map {
                val dttmWithZone = toAnotherZone(it.dttm, ZoneId.systemDefault())
                it.toDto().apply {
                    //FIXME: Change this to support time zone appointments
                    dttmZoned = dttmWithZone
                    dttmTxt = toISODateWithOffset(dttmWithZone)
                }
            }
    }

    @Transactional(readOnly = true)
    override fun getAllClientAppointments(clientIdentifier: UUID): List<AppointmentDto> {

        LOGGER.info("opr='getAllClientAppointments', msg='Get client appointments', clientIdentifier={}",
            clientIdentifier)

        val client = clientOperations.getClient(clientIdentifier)

        return appointmentRepository.findAppointmentsByClient(client.uuid)
            .map {
                val dttmWithZone = toAnotherZone(it.dttm, ZoneId.systemDefault())
                it.toDto().apply {
                    //FIXME: Change this to support time zone appointments
                    dttmZoned = dttmWithZone
                    dttmTxt = toISODateWithOffset(dttmWithZone)
                }
            }

    }

    @Transactional(readOnly = true)
    override fun getAppointments(coachIdentifier: UUID, clientIdentifier: UUID): List<AppointmentDto> {

        LOGGER.info("opr='getAppointments', msg='Get appointments', coachIdentifier={}, clientIdentifier={}",
            coachIdentifier,
            clientIdentifier)

        val coach = coachOperations.getCoach(coachIdentifier)
        val client = coach.clients.firstOrNull { it.uuid == clientIdentifier }
            ?: throw CustomerNotFoundException("Could not find any client with uuid: $clientIdentifier.")

        return appointmentRepository.findAppointments(coach.uuid, client.uuid)
            .map {
                val dttmWithZone = toAnotherZone(it.dttm, ZoneId.systemDefault())
                it.toDto().apply {
                    //FIXME: Change this to support time zone appointments
                    dttmZoned = dttmWithZone
                    dttmTxt = toISODateWithOffset(dttmWithZone)
                }
            }
    }

    @Transactional
    override fun updateAppointment(appointmentDto: AppointmentDto): AppointmentDto {

        checkNotNull(appointmentDto.dttmTxt) { "dttmTxt can not be null" }

        LOGGER.info("opr='updateAppointment', msg='Update appointments'")

        val appointmentEntity = appointmentRepository.findByUuidAndDeleteFalse(appointmentDto.identifier) ?: run {
            LOGGER.warn("opr='updateAppointment', msg='Appointment doesn't exist', appointment={}",
                appointmentDto.identifier)
            throw AppointmentNotFoundException("Appointment not found, please check the identifier.")
        }

        val zonedDateTime = toZonedDateTime(appointmentDto.dttmTxt!!)
        val dttmZonedSystem = toAnotherZone(zonedDateTime, ZoneId.systemDefault()).toLocalDateTime()

        val appointmentToPersist = appointmentEntity
            .apply {
                dttm = dttmZonedSystem
                price = appointmentDto.price
                currency = appointmentDto.currency.currencyCode
                notes = appointmentDto.notes
            }

        LOGGER.debug("opr='updateAppointment', msg='Appointment to persist', appointment={}", appointmentToPersist)

        val appointmentPersisted = appointmentRepository.save(appointmentToPersist)
            .toDto()
            .apply {
                this.dttmTxt = appointmentDto.dttmTxt
                this.dttmZoned = zonedDateTime
            }

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
