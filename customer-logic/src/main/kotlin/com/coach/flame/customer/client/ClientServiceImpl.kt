package com.coach.flame.customer.client

import com.coach.flame.customer.CustomerNotFoundException
import com.coach.flame.customer.CustomerRetrieveException
import com.coach.flame.domain.*
import com.coach.flame.domain.converters.ClientDtoConverter
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.ClientStatus
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.CoachRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ClientServiceImpl(
    private val clientRepository: ClientRepository,
    private val coachRepository: CoachRepository,
    private val clientDtoConverter: ClientDtoConverter,
) : ClientService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientServiceImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun getAllClients(): Set<ClientDto> {
        return clientRepository.findAll()
            .map { clientDtoConverter.convert(it) }
            .toSet()
    }

    @Transactional(readOnly = true)
    override fun getAllClientsFromCoach(uuid: UUID): Set<ClientDto> {
        return clientRepository.findClientsWithCoach(uuid)
            .map { clientDtoConverter.convert(it) }
            .toSet()
    }

    @Transactional(readOnly = true)
    override fun getAllClientsForCoach(uuid: UUID): Set<ClientDto> {
        return clientRepository.findClientsForCoach(uuid.toString())
            .map { clientDtoConverter.convert(it) }
            .toSet()
    }

    @Transactional
    override fun updateClientStatus(uuid: UUID, status: ClientStatusDto): ClientDto {

        val client = getCustomer(uuid, CustomerTypeDto.CLIENT) as Client

        LOGGER.info("opr='updateClientStatus', msg='Client found', uuid={}", client.uuid)

        client.clientStatus = ClientStatus.valueOf(status.name)

        val clientUpdated = clientRepository.save(client)

        LOGGER.info("opr='updateClientStatus', msg='Client new status', status={}", clientUpdated.clientStatus)

        return clientDtoConverter.convert(clientUpdated)

    }

    @Transactional
    override fun updateClientCoach(uuidClient: UUID, uuidCoach: UUID): ClientDto {

        val client = getCustomer(uuidClient, CustomerTypeDto.CLIENT) as Client
        LOGGER.info("opr='updateClientStatus', msg='Client found', uuid={}", client.uuid)

        val coach = getCustomer(uuidCoach, CustomerTypeDto.COACH) as Coach
        LOGGER.info("opr='updateClientStatus', msg='Coach found', uuid={}", coach.uuid)

        client.coach = coach

        val clientUpdated = clientRepository.save(client)

        LOGGER.info("opr='updateClientStatus', msg='Client new coach', coach={}", clientUpdated.coach?.uuid)

        return clientDtoConverter.convert(clientUpdated)

    }

    /**
     * Auxiliary method to get the customer. When the param type is [CustomerTypeDto.CLIENT] method will return
     * [Client], and when is [CustomerTypeDto.COACH] will return [Coach]. If the type is different then the two values
     * the method throws a [CustomerRetrieveException]
     *
     * @param uuid - Customer identifier
     * @param type - Customer type
     * @return a customer
     * @throws CustomerNotFoundException when didn't find any customer
     * @throws CustomerRetrieveException when received an invalid customer type
     */
    private fun getCustomer(uuid: UUID, type: CustomerTypeDto): Any {

        return when (type) {
            CustomerTypeDto.CLIENT -> {
                clientRepository.findByUuid(uuid)
                    ?: throw CustomerNotFoundException("Could not found any client with uuid: $uuid")
            }
            CustomerTypeDto.COACH -> {
                coachRepository.findByUuid(uuid)
                    ?: throw CustomerNotFoundException("Could not found any coach with uuid: $uuid")
            }
            else -> throw CustomerRetrieveException("$type is a invalid customer type")
        }

    }

}