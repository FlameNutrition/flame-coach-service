package com.coach.flame.api.client

import com.coach.flame.api.client.request.ClientRequest
import com.coach.flame.api.client.request.ClientRequestConverter
import com.coach.flame.api.client.response.ClientResponse
import com.coach.flame.api.client.response.ClientResponseConverter
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.client.ClientService
import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/client")
class ClientImp(
    private val clientService: ClientService,
    private val clientRequestConverter: ClientRequestConverter,
    private val clientResponseConverter: ClientResponseConverter,
) : ClientAPI {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientImp::class.java)
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/create")
    @ResponseBody
    override fun registerNewClient(@RequestBody(required = true) client: ClientRequest): ClientResponse {
        try {

            requireNotNull(client.firstname) { "Missing required parameter request: firstname" }
            requireNotNull(client.lastname) { "Missing required parameter request: lastname" }
            requireNotNull(client.email) { "Missing required parameter request: email" }
            requireNotNull(client.password) { "Missing required parameter request: password" }
            requireNotNull(client.type) { "Missing required parameter request: type" }

            val clientDomain = clientRequestConverter.convert(client)

            val clientPersisted = clientService.registerClient(clientDomain)

            return clientResponseConverter.convert(clientPersisted)

        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='registerNewClient', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='registerNewClient', msg='Please check following problem'", ex)
                    throw RestException(ex)
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/newSession")
    @ResponseBody
    override fun getNewClientSession(@RequestBody(required = true) client: ClientRequest): ClientResponse {
        try {

            requireNotNull(client.email) { "Missing required parameter request: email" }
            requireNotNull(client.password) { "Missing required parameter request: password" }

            //FIXME: Send the password encrypted
            val clientPersisted = clientService.getNewClientSession(client.email, client.password)

            return clientResponseConverter.convert(clientPersisted)

        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='getNewClientSession', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='getNewClientSession', msg='Please check following problem'", ex)
                    throw RestException(ex)
                }
                else -> {
                    throw ex
                }
            }
        }
    }

}