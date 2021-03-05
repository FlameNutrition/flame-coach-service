package com.coach.flame.client

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.converters.ClientDtoConverter
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.User
import com.coach.flame.jpa.entity.UserSession
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.ClientTypeRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class ClientServiceImpl(
    private val clientRepository: ClientRepository,
    private val clientTypeRepository: ClientTypeRepository,
    private val clientDtoConverter: ClientDtoConverter,
) : ClientService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientServiceImpl::class.java)
    }

    override fun getClient(uuid: UUID): ClientDto {

        LOGGER.info("opr='getClient', msg='Get client by uuid', uuid=$uuid")

        val client =
            clientRepository.findByUuid(uuid)
                ?: throw ClientNotFoundException("Could not found any client with uuid: $uuid")

        return clientDtoConverter.convert(client)

    }

    @Transactional
    override fun registerClient(clientDto: ClientDto): ClientDto {

        LOGGER.info("opr='registerClient', msg='Register a new client'")

        try {
            val clientType = clientTypeRepository.getByType(clientDto.clientType.name)

            val expirationDate = LocalDateTime.now().plusHours(2)

            val entity = Client(
                uuid = clientDto.identifier,
                firstName = clientDto.firstName,
                lastName = clientDto.lastName,
                clientType = clientType,
                user = User(
                    email = clientDto.loginInfo!!.username,
                    //TODO: Encrypt password
                    password = clientDto.loginInfo!!.password
                ),
                userSession = UserSession(
                    expirationDate = expirationDate,
                    token = UUID.randomUUID()
                )
            )

            val client = clientRepository.saveAndFlush(entity)

            return clientDtoConverter.convert(client)
        } catch (ex: Exception) {
            LOGGER.error("opr='registerClient'", ex)
            when (ex) {
                is DataIntegrityViolationException -> throw ClientRegisterDuplicateException("The following client already exists")
                else -> throw ClientRegisterException("Problem occurred when try to register a new client")
            }
        }
    }

}