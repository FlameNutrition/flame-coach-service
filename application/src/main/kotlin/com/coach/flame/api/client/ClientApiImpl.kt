package com.coach.flame.api.client

import com.coach.flame.api.client.request.ContactInfoRequest
import com.coach.flame.api.client.request.PersonalDataRequest
import com.coach.flame.api.client.response.ClientInviteResponse
import com.coach.flame.api.client.response.Config
import com.coach.flame.api.client.response.ContactInfoResponse
import com.coach.flame.api.client.response.PersonalDataResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.configs.ConfigsService
import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.register.RegistrationCustomerService
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.MeasureTypeDto
import com.coach.flame.exception.RestInvalidRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/client")
class ClientApiImpl(
    private val customerService: CustomerService,
    private val configsService: ConfigsService,
    private val registrationCustomerService: RegistrationCustomerService,
) : ClientApi {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClientApiImpl::class.java)
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/invite")
    @ResponseBody
    override fun registrationInvite(
        @RequestParam("coachIdentifier") coachIdentifier: UUID?,
        @RequestParam("clientEmail") clientEmail: String?,
    ): ClientInviteResponse {

        try {
            requireNotNull(coachIdentifier) { "missing required parameter: coachIdentifier" }
            requireNotNull(clientEmail) { "missing required parameter: clientEmail" }
            require(clientEmail.isNotBlank()) { "empty/blank required parameter: clientEmail" }

            val coach = customerService.getCustomer(coachIdentifier, CustomerTypeDto.COACH) as CoachDto

            val registrationInviteDto = registrationCustomerService.sendRegistrationLink(coach, clientEmail)

            return ClientInviteResponse(
                coachIdentifier = registrationInviteDto.sender.identifier,
                registrationLink = registrationInviteDto.registrationLink,
                registrationKey = registrationInviteDto.registrationKey)
        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='registrationInvite', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex.localizedMessage, ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/getContactInformation")
    @ResponseBody
    override fun getContactInformation(@RequestHeader("clientIdentifier") identifier: UUID): ContactInfoResponse {

        val client = customerService.getCustomer(identifier, CustomerTypeDto.CLIENT)

        return ContactInfoResponse(
            identifier = client.identifier,
            firstName = client.firstName,
            lastName = client.lastName,
            phoneCode = client.phoneCode,
            phoneNumber = client.phoneNumber,
            country = client.country?.let { Config(it.countryCode, it.externalValue) },
        )

    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/updateContactInformation")
    @ResponseBody
    override fun updateContactInformation(
        @RequestHeader("clientIdentifier") identifier: UUID,
        @RequestBody(required = true) request: ContactInfoRequest,
    ): ContactInfoResponse {

        val clientOldContactInformation =
            customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) as ClientDto

        val clientNewContactInformation = clientOldContactInformation.copy(
            identifier = clientOldContactInformation.identifier,
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

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/getPersonalData")
    @ResponseBody
    override fun getPersonalData(@RequestHeader("clientIdentifier") identifier: UUID): PersonalDataResponse {

        val client = customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) as ClientDto

        return PersonalDataResponse(
            weight = client.weight,
            height = client.height,
            gender = client.gender?.let { Config(it.genderCode, it.externalValue) },
            measureType = Config(client.measureType.code, client.measureType.value),
        )
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/updatePersonalData")
    @ResponseBody
    override fun updatePersonalData(
        @RequestHeader("clientIdentifier") identifier: UUID,
        @RequestBody(required = true) request: PersonalDataRequest,
    ): PersonalDataResponse {
        try {
            val client = customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) as ClientDto

            val measureType: MeasureTypeDto = try {
                MeasureTypeDto.valueOf(request.measureTypeCode)
            } catch (ex: Exception) {
                throw IllegalArgumentException("measureTypeCode invalid")
            }

            val newClient = customerService.updateCustomer(identifier, client.copy(
                weight = request.weight,
                height = request.height,
                gender = request.genderCode?.let { configsService.getGender(request.genderCode) },
                measureType = measureType)) as ClientDto


            return PersonalDataResponse(
                weight = newClient.weight,
                height = newClient.height,
                gender = newClient.gender?.let { Config(it.genderCode, it.externalValue) },
                measureType = Config(newClient.measureType.code, newClient.measureType.value),
            )
        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='updatePersonalData', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex.localizedMessage, ex)
        }
    }
}
