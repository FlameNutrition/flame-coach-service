package com.coach.flame.client

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientTypeDto
import com.coach.flame.domain.CountryDto
import com.coach.flame.domain.GenderDto
import com.coach.flame.domain.converters.ClientDtoConverter
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.repository.ClientRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ClientServiceImpl(
    @Autowired private val clientRepository: ClientRepository,
    @Autowired private val clientDtoConverter: ClientDtoConverter
) : ClientService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientServiceImpl::class.java)
    }

    override fun getClient(uuid: UUID): ClientDto {

        LOGGER.info("opr='getClient', msg='Get client by uuid', uuid=$uuid")

        val client = clientRepository.findByUuid(uuid) ?: throw ClientNotFound("Could not found any client with uuid: $uuid")

        return clientDtoConverter.convert(client)

    }

    override fun registerClient(clientDto: ClientDto) {
        TODO("Not yet implemented")
    }

}