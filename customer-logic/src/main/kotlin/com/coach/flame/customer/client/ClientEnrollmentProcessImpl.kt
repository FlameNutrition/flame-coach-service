package com.coach.flame.customer.client

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientStatusDto
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
    override fun init(client: UUID, coach: UUID): ClientDto {

        LOGGER.info("opr='init', msg='Init the client enrollment process'")

        //TODO: Logic to check if you can start the process

        clientService.updateClientStatus(client, ClientStatusDto.PENDING)
        val clientDto = clientService.updateClientCoach(client, coach)

        LOGGER.info("opr='init', msg='Client enrollment process status.', client={}, coach={}, status={}",
            clientDto.identifier, coach, clientDto.clientStatus)

        return clientDto

    }

    override fun finish(client: UUID): ClientDto {

        LOGGER.info("opr='finish', msg='Finish the client enrollment process'")

        //TODO: Logic to check if you can finish the process

        val clientDto = clientService.updateClientStatus(client, ClientStatusDto.ACCEPTED)

        LOGGER.info("opr='finish', msg='Client enrollment process status.', client={}, status={}",
            clientDto.identifier, clientDto.clientStatus)

        return clientDto
    }

}