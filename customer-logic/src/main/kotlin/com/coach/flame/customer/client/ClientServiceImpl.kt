package com.coach.flame.customer.client

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.converters.ClientToClientDtoConverter
import com.coach.flame.jpa.repository.ClientRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ClientServiceImpl(
    private val clientRepository: ClientRepository,
    private val clientToClientDtoConverter: ClientToClientDtoConverter,
) : ClientService {

    @Transactional(readOnly = true)
    override fun getAllClients(): Set<ClientDto> {
        return clientRepository.findAll()
            .map { clientToClientDtoConverter.convert(it) }
            .toSet()
    }

    @Transactional(readOnly = true)
    override fun getAllClientsForCoach(uuid: UUID): Set<ClientDto> {
        return clientRepository.findClientsWithCoach(uuid)
            .map { clientToClientDtoConverter.convert(it) }
            .toSet()
    }

    @Transactional(readOnly = true)
    override fun getAllClientsAvailableForCoaches(): Set<ClientDto> {
        return clientRepository.findClientsWithoutCoach()
            .map { clientToClientDtoConverter.convert(it) }
            .toSet()
    }


}