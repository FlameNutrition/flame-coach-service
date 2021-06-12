package com.coach.flame.customer.client

import com.coach.flame.customer.register.RegistrationCustomerService
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.InviteInfoDto
import com.coach.flame.jpa.repository.ClientRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InviteComponent(
    private val clientEnrollmentProcess: ClientEnrollmentProcess,
    private val registrationCustomerService: RegistrationCustomerService,
    private val clientRepository: ClientRepository,
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(InviteComponent::class.java)
    }

    /**
     * Send an invite to client, if the client is on the system this component will
     * trigger the enrollment process [ClientEnrollmentProcess.init].
     * Otherwise will send a registration link to client [RegistrationCustomerService.sendRegistrationLink].
     *
     * @param coachDto Coach domain object
     * @param emailClient Client email
     *
     * @see ClientEnrollmentProcess
     * @see RegistrationCustomerService
     *
     * @return Information about the invite
     */
    fun send(coachDto: CoachDto, emailClient: String): InviteInfoDto {

        val client = clientRepository.findByUserEmailIs(emailClient)

        return if (client !== null) {

            LOGGER.info("opr='send', msg='Client email found, init the enrollment process'")

            val clientDto = clientEnrollmentProcess.init(client.toDto(), coachDto.identifier)

            InviteInfoDto(coachDto.identifier, false).apply {
                clientStatus = clientDto.clientStatus
            }
        } else {

            LOGGER.info("opr='send', msg='Client email not found, send registration link'")

            val registrationInfoDto = registrationCustomerService.sendRegistrationLink(coachDto, emailClient)

            InviteInfoDto(coachDto.identifier, true).apply {
                registrationLink = registrationInfoDto.registrationLink
                registrationKey = registrationInfoDto.registrationKey
            }
        }

    }

}
