package com.coach.flame.api.client

import com.coach.flame.api.client.request.MeasureRequest
import com.coach.flame.api.client.response.Measure
import com.coach.flame.api.client.response.MeasureResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.measures.MeasureFactory
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.MeasureDto
import com.coach.flame.exception.RestInvalidRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/client/measures")
class MeasureApiImpl(
    private val customerService: CustomerService,
    private val measureFactory: MeasureFactory,
) : MeasureApi {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(MeasureApiImpl::class.java)
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/weight/get")
    @ResponseBody
    override fun getWeights(@RequestParam("clientIdentifier") clientIdentifier: UUID?): MeasureResponse {
        try {
            requireNotNull(clientIdentifier) { "missing required parameter: uuid" }

            val client = getClient(clientIdentifier)

            val clientUpdated = measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.GET,
                client)

            return MeasureResponse(
                weights = clientUpdated.weightMeasureTimeline
                    .map { Measure(it.id!!, it.date, it.value) }
                    .toList())

        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='getWeights', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex.localizedMessage, ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/weight/add")
    @ResponseBody
    override fun addWeight(
        @RequestBody(required = true) request: MeasureRequest,
        @RequestParam("clientIdentifier") clientIdentifier: UUID?,
    ): MeasureResponse {

        try {
            requireNotNull(clientIdentifier) { "missing required parameter: uuid" }
            requireNotNull(request.value) { "missing required parameter: value" }

            val client = getClient(clientIdentifier)

            val clientUpdated = measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.ADD,
                client,
                MeasureDto(
                    id = null,
                    date = request.date ?: LocalDate.now(),
                    value = request.value))

            return MeasureResponse(
                weights = clientUpdated.weightMeasureTimeline
                    .map { Measure(it.id!!, it.date, it.value) }
                    .toList())

        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='addWeight', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex.localizedMessage, ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/weight/edit")
    @ResponseBody
    override fun editWeight(
        @RequestBody(required = true) request: MeasureRequest,
        @RequestParam("clientIdentifier") clientIdentifier: UUID?,
        @RequestParam("identifier") id: Long?,
    ): MeasureResponse {
        try {
            requireNotNull(clientIdentifier) { "missing required parameter: uuid" }
            requireNotNull(request.value) { "missing required parameter: value" }
            requireNotNull(id) { "missing required parameter: identifier" }

            val client = getClient(clientIdentifier)

            val clientUpdated = measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.UPDATE,
                client,
                MeasureDto(
                    id = id,
                    date = request.date ?: LocalDate.now(),
                    value = request.value))

            return MeasureResponse(
                weights = clientUpdated.weightMeasureTimeline
                    .map { Measure(it.id!!, it.date, it.value) }
                    .toList())

        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='editWeight', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex.localizedMessage, ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @DeleteMapping("/weight/delete")
    @ResponseBody
    override fun deleteWeight(
        @RequestParam("clientIdentifier") clientIdentifier: UUID?,
        @RequestParam("identifier") id: Long?,
    ): MeasureResponse {
        try {
            requireNotNull(clientIdentifier) { "missing required parameter: uuid" }
            requireNotNull(id) { "missing required parameter: identifier" }

            val client = getClient(clientIdentifier)

            val clientUpdated = measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.DELETE,
                client,
                //Passed any value, the only field important is the id
                MeasureDto(
                    id = id,
                    date = LocalDate.now(),
                    value = 0.0f))

            return MeasureResponse(
                weights = clientUpdated.weightMeasureTimeline
                    .map { Measure(it.id!!, it.date, it.value) }
                    .toList())

        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='deleteWeight', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex.localizedMessage, ex)
        }
    }

    private fun getClient(clientIdentifier: UUID): ClientDto {
        return customerService.getCustomer(clientIdentifier, CustomerTypeDto.CLIENT) as ClientDto
    }

}
