package com.coach.flame.customer.client

import com.coach.flame.customer.CustomerNotFoundException
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientStatusDto
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
    private val coachRepository: CoachRepository
) : ClientService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientServiceImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun getAllClients(): Set<ClientDto> {
        return clientRepository.findAll()
            .map { it.toDto(it.coach?.toDto()) }
            .toSet()
    }

    @Transactional(readOnly = true)
    override fun getAllClientsFromCoach(uuid: UUID): Set<ClientDto> {
        return clientRepository.findClientsWithCoach(uuid)
            .map { it.toDto(it.coach?.toDto()) }
            .toSet()
    }

    @Transactional(readOnly = true)
    override fun getAllClientsForCoach(uuid: UUID): Set<ClientDto> {
        return clientRepository.findClientsForCoach(uuid.toString())
            .map { it.toDto(it.coach?.toDto()) }
            .toSet()
    }

    @Transactional
    override fun updateClientStatus(uuid: UUID, status: ClientStatusDto): ClientDto {

        val client = getClient(uuid)

        LOGGER.info("opr='updateClientStatus', msg='Client found', uuid={}", client.uuid)

        client.clientStatus = ClientStatus.valueOf(status.name)

        val clientUpdated = clientRepository.save(client)
        val coach = clientUpdated.coach

        LOGGER.info("opr='updateClientStatus', msg='Client new status', status={}", clientUpdated.clientStatus)

        return clientUpdated.toDto(coach?.toDto())

    }

    @Transactional
    override fun linkCoach(uuidClient: UUID, uuidCoach: UUID): ClientDto {

        val client = getClient(uuidClient)
        LOGGER.info("opr='linkCoach', msg='Client found', uuid={}", client.uuid)

        val coach = getCoach(uuidCoach)
        LOGGER.info("opr='linkCoach', msg='Coach found', uuid={}", coach.uuid)

        client.coach = coach

        val clientUpdated = clientRepository.save(client)

        LOGGER.info("opr='linkCoach', msg='Client new coach', coach={}", clientUpdated.coach!!.uuid)

        return clientUpdated.toDto(clientUpdated.coach?.toDto())

    }

    @Transactional
    override fun unlinkCoach(uuidClient: UUID): ClientDto {

        val client = getClient(uuidClient)
        LOGGER.info("opr='unlinkCoach', msg='Client found', uuid={}", client.uuid)

        client.coach = null
        client.clientStatus = ClientStatus.AVAILABLE

        val clientUpdated = clientRepository.save(client)

        LOGGER.info("opr='unlinkCoach', msg='Coach unlinked from a client', client={}", clientUpdated.uuid)

        return clientUpdated.toDto(client.coach?.toDto())
    }

    /**
     * Auxiliary method to get the client entity. If the entity doesn't exist the method will raise a
     * [CustomerNotFoundException] exception
     *
     * @param uuid - Client uuid identifier
     * @return a client entity
     * @throws CustomerNotFoundException when didn't find any client
     */
    private fun getClient(uuid: UUID): Client {
        return clientRepository.findByUuid(uuid)
            ?: throw CustomerNotFoundException("Could not found any client with uuid: $uuid")
    }

    /**
     * Auxiliary method to get the coach entity. If the entity doesn't exist the method will raise a
     * [CustomerNotFoundException] exception
     *
     * @param uuid - Coach uuid identifier
     * @return a coach entity
     * @throws CustomerNotFoundException when didn't find any client
     */
    private fun getCoach(uuid: UUID): Coach {
        return coachRepository.findByUuid(uuid)
            ?: throw CustomerNotFoundException("Could not found any coach with uuid: $uuid")
    }

}
