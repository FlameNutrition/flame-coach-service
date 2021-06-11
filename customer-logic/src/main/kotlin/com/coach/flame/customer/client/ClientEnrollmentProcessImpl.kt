package com.coach.flame.customer.client

import com.coach.flame.customer.EnrollmentProcessException
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.failure.domain.ErrorCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class ClientEnrollmentProcessImpl(
    private val clientService: ClientService,
) : ClientEnrollmentProcess {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientEnrollmentProcess::class.java)
    }

    @Transactional
    override fun init(client: ClientDto, coach: UUID): ClientDto {

        LOGGER.info("opr='init', msg='Init the client enrollment process'")

        if (ClientStatusDto.AVAILABLE == client.clientStatus) {
            clientService.updateClientStatus(client.identifier, ClientStatusDto.PENDING)
            val clientDto = clientService.linkCoach(client.identifier, coach)

            LOGGER.info("opr='init', msg='Client enrollment process status.', client={}, coach={}, status={}",
                clientDto.identifier, clientDto.coach?.identifier, clientDto.clientStatus)

            return clientDto
        }

        throw EnrollmentProcessException(ErrorCode.CODE_3001, "Client already has a coach assigned.")

    }

    override fun finish(client: ClientDto, accept: Boolean): ClientDto {

        LOGGER.info("opr='finish', msg='Finish the client enrollment process', accept={}", accept)

        if (accept) {

            if (ClientStatusDto.PENDING == client.clientStatus) {
                val clientDto = clientService.updateClientStatus(client.identifier, ClientStatusDto.ACCEPTED)

                LOGGER.info("opr='finish', msg='Client enrollment process status.', client={}, status={}",
                    clientDto.identifier, clientDto.clientStatus)

                return clientDto
            }

            throw EnrollmentProcessException(ErrorCode.CODE_3002,
                "Client didn't start the enrollment process or already has a coach assigned.")
        } else {
            return clientService.unlinkCoach(client.identifier)
        }
    }

    override fun `break`(client: ClientDto): ClientDto {

        LOGGER.info("opr='break', msg='Break the link between the client and coach'")
        return clientService.unlinkCoach(client.identifier)

    }

}
