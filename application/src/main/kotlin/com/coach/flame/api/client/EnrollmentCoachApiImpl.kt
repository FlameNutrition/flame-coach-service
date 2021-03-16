package com.coach.flame.api.client

import com.coach.flame.api.client.request.EnrollmentRequest
import com.coach.flame.api.client.response.EnrollmentResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.customer.client.ClientEnrollmentProcess
import com.coach.flame.domain.ClientDto
import com.coach.flame.exception.RestInvalidRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/client/enrollment")
class EnrollmentCoachApiImpl(
    private val enrollmentProcess: ClientEnrollmentProcess,
) : EnrollmentCoachApi {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(EnrollmentCoachApiImpl::class.java)

        private val converter = { client: ClientDto ->
            EnrollmentResponse(
                coach = client.coach?.identifier,
                status = client.clientStatus!!.name,
                client = client.identifier
            )
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/init")
    @ResponseBody
    override fun init(@RequestBody(required = true) enrollmentRequest: EnrollmentRequest): EnrollmentResponse {
        try {
            requireNotNull(enrollmentRequest.coach) { "Missing required parameter request: coach" }
            requireNotNull(enrollmentRequest.client) { "Missing required parameter request: client" }

            val client = enrollmentProcess.init(enrollmentRequest.client, enrollmentRequest.coach)

            return converter(client)
        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='init', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/finish")
    @ResponseBody
    override fun finish(@RequestBody(required = true) enrollmentRequest: EnrollmentRequest): EnrollmentResponse {
        try {
            requireNotNull(enrollmentRequest.client) { "Missing required parameter request: client" }
            requireNotNull(enrollmentRequest.accept) { "Missing required parameter request: accept" }

            val client = enrollmentProcess.finish(enrollmentRequest.client, enrollmentRequest.accept)
            return converter(client)
        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='init', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/break")
    @ResponseBody
    override fun `break`(@RequestBody(required = true) enrollmentRequest: EnrollmentRequest): EnrollmentResponse {
        try {
            requireNotNull(enrollmentRequest.client) { "Missing required parameter request: client" }

            val client = enrollmentProcess.`break`(enrollmentRequest.client)
            return converter(client)
        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='init', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex)
        }
    }
}