package com.coach.flame.api.coach

import com.coach.flame.api.coach.request.ContactInfoRequest
import com.coach.flame.api.coach.response.ClientCoach
import com.coach.flame.api.coach.response.CoachResponse
import com.coach.flame.api.coach.response.Config
import com.coach.flame.api.coach.response.ContactInfoResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.configs.ConfigsService
import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.coach.CoachService
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.exception.RestInvalidRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/coach")
class CoachApiImpl(
    private val customerService: CustomerService,
    private val coachService: CoachService,
    private val configsService: ConfigsService,
) : CoachApi {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CoachApiImpl::class.java)

        private val convertToCoachResponse = { coach: CoachDto ->
            CoachResponse(
                identifier = coach.identifier,
                clientsCoach = coach.listOfClients
                    .map {
                        ClientCoach(
                            firstname = it.firstName,
                            lastname = it.lastName,
                            identifier = it.identifier,
                            status = it.clientStatus!!.name,
                            email = it.loginInfo!!.username,
                            registrationDate = it.registrationDate)
                    }.toSet()
            )
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/getClientsAccepted")
    @ResponseBody
    override fun getClientsCoach(@RequestParam(required = true) identifier: String): CoachResponse {
        try {
            val coach = coachService.getCoachWithClientsAccepted(UUID.fromString(identifier))
            return convertToCoachResponse(coach)
        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='getClientsCoach', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex.localizedMessage, ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/getClientsCoachPlusClientsAvailable")
    @ResponseBody
    override fun getClientsCoachPlusClientsAvailable(@RequestParam(required = true) identifier: String): CoachResponse {
        try {
            val coach = coachService.getCoachWithClientsAvailable(UUID.fromString(identifier))
            return convertToCoachResponse(coach)
        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='getClientsCoachPlusClientsAvailable', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex.localizedMessage, ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/getContactInformation")
    @ResponseBody
    override fun getContactInformation(@RequestHeader("coachIdentifier") identifier: UUID): ContactInfoResponse {

        val coach = customerService.getCustomer(identifier, CustomerTypeDto.COACH)

        return ContactInfoResponse(
            identifier = coach.identifier,
            firstName = coach.firstName,
            lastName = coach.lastName,
            phoneCode = coach.phoneCode,
            phoneNumber = coach.phoneNumber,
            country = coach.country?.let { Config(it.countryCode, it.externalValue) },
        )

    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/updateContactInformation")
    @ResponseBody
    override fun updateContactInformation(
        @RequestHeader("coachIdentifier") identifier: UUID,
        @RequestBody(required = true) request: ContactInfoRequest,
    ): ContactInfoResponse {

        val coachOldContactInformation =
            customerService.getCustomer(identifier, CustomerTypeDto.COACH) as CoachDto

        val clientNewContactInformation = coachOldContactInformation.copy(
            identifier = coachOldContactInformation.identifier,
            firstName = request.firstName,
            lastName = request.lastName,
            phoneCode = request.phoneCode,
            phoneNumber = request.phoneNumber,
            country = request.countryCode?.let { configsService.getCountry(request.countryCode) },
        )

        val client = customerService.updateCustomer(identifier, clientNewContactInformation)

        return ContactInfoResponse(
            identifier = client.identifier,
            firstName = client.firstName,
            lastName = client.lastName,
            phoneCode = client.phoneCode,
            phoneNumber = client.phoneNumber,
            country = client.country?.let { Config(it.countryCode, it.externalValue) },
        )

    }
}
