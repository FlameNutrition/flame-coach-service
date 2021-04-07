package com.coach.flame.customer.client

import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.EnrollmentProcessException
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.CustomerTypeDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class ClientEnrollmentProcessImpl(
    private val clientService: ClientService,
    private val customerService: CustomerService,
) : ClientEnrollmentProcess {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientEnrollmentProcess::class.java)
    }

    @Transactional
    override fun init(client: UUID, coach: UUID): ClientDto {

        LOGGER.info("opr='init', msg='Init the client enrollment process'")

        val dbClient = customerService.getCustomer(client, CustomerTypeDto.CLIENT) as ClientDto

        if (ClientStatusDto.AVAILABLE == dbClient.clientStatus) {
            clientService.updateClientStatus(client, ClientStatusDto.PENDING)
            val clientDto = clientService.linkCoach(client, coach)

            LOGGER.info("opr='init', msg='Client enrollment process status.', client={}, coach={}, status={}",
                clientDto.identifier, clientDto.coach?.identifier, clientDto.clientStatus)

            return clientDto
        }

        throw EnrollmentProcessException("Client already has a coach assigned.")

    }

    override fun finish(client: UUID, accept: Boolean): ClientDto {

        LOGGER.info("opr='finish', msg='Finish the client enrollment process', accept={}", accept)

        if (accept) {
            val dbClient = customerService.getCustomer(client, CustomerTypeDto.CLIENT) as ClientDto

            if (ClientStatusDto.PENDING == dbClient.clientStatus) {
                val clientDto = clientService.updateClientStatus(client, ClientStatusDto.ACCEPTED)

                LOGGER.info("opr='finish', msg='Client enrollment process status.', client={}, status={}",
                    clientDto.identifier, clientDto.clientStatus)

                return clientDto
            }

            throw EnrollmentProcessException("Client didn't start the enrollment process or already has a coach assigned.")
        } else {
            return clientService.unlinkCoach(client)
        }
    }

    override fun `break`(client: UUID): ClientDto {

        LOGGER.info("opr='break', msg='Break the link between the client and coach'")
        return clientService.unlinkCoach(client)

    }

    override fun status(client: UUID): ClientDto {

        LOGGER.info("opr='status', msg='Get enrollment status', client={}", client)
        return customerService.getCustomer(client, CustomerTypeDto.CLIENT) as ClientDto

    }

}
