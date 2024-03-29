package com.coach.flame.api.client

import com.coach.flame.api.client.request.ContactInfoRequestBuilder
import com.coach.flame.api.client.request.ContactInfoRequestMaker
import com.coach.flame.api.client.request.PersonalDataRequestBuilder
import com.coach.flame.api.client.request.PersonalDataRequestMaker
import com.coach.flame.configs.ConfigsService
import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.client.InviteComponent
import com.coach.flame.customer.register.RegistrationCustomerService
import com.coach.flame.domain.*
import com.coach.flame.domain.maker.*
import com.coach.flame.exception.RestInvalidRequestException
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientApiImplTest {

    @MockK
    private lateinit var customerService: CustomerService

    @MockK
    private lateinit var configsService: ConfigsService

    @MockK
    private lateinit var inviteComponent: InviteComponent

    @InjectMockKs
    private lateinit var classToTest: ClientApiImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get contact info`() {

        val identifier = UUID.randomUUID()
        val client = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
                with(ClientDtoMaker.country, CountryDtoBuilder.default()))
            .make()
        every { customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) } returns client

        val response = classToTest.getContactInformation(identifier)

        then(response.firstName).isEqualTo(client.firstName)
        then(response.lastName).isEqualTo(client.lastName)
        then(response.identifier).isEqualTo(client.identifier)
        then(response.phoneCode).isEqualTo(client.phoneCode)
        then(response.phoneNumber).isEqualTo(client.phoneNumber)
        then(response.country?.code).isEqualTo(client.country?.countryCode)
        then(response.country?.value).isEqualTo(client.country?.externalValue)

    }

    @Test
    fun `test update contact info`() {

        val identifier = UUID.randomUUID()
        val request = ContactInfoRequestBuilder.maker()
            .but(with(ContactInfoRequestMaker.countryCode, "PT"))
            .make()
        val countryDto = CountryDtoBuilder.maker()
            .but(with(CountryDtoMaker.countryCode, "PT"),
                with(CountryDtoMaker.externalValue, "Portugal"))
            .make()
        val client = slot<ClientDto>()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.identifier, identifier),
                with(ClientDtoMaker.coach, CoachDtoBuilder.default()),
                with(ClientDtoMaker.gender, GenderDtoBuilder.default()),
                with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
                with(ClientDtoMaker.country, countryDto))
            .make()

        every { customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) } returns clientDto
        every { configsService.getCountry("PT") } returns countryDto
        every { customerService.updateCustomer(identifier, capture(client)) } answers { client.captured }

        val response = classToTest.updateContactInformation(identifier, request)

        then(response.firstName).isEqualTo(request.firstName)
        then(response.lastName).isEqualTo(response.lastName)
        then(response.identifier).isEqualTo(identifier)
        then(response.phoneCode).isEqualTo(request.phoneCode)
        then(response.phoneNumber).isEqualTo(request.phoneNumber)
        then(response.country?.code).isEqualTo(request.countryCode)
        then(response.country?.value).isEqualTo("Portugal")

        then(client.captured.birthday).isEqualTo(clientDto.birthday)
        then(client.captured.coach).isEqualTo(clientDto.coach)
        then(client.captured.loginInfo).isEqualTo(clientDto.loginInfo)
        then(client.captured.gender).isEqualTo(clientDto.gender)
        then(client.captured.customerType).isEqualTo(clientDto.customerType)
        then(client.captured.registrationDate).isEqualTo(clientDto.registrationDate)
        then(client.captured.weight).isEqualTo(clientDto.weight)
        then(client.captured.height).isEqualTo(clientDto.height)
        then(client.captured.measureType).isEqualTo(clientDto.measureType)
        then(client.captured.clientStatus).isEqualTo(clientDto.clientStatus)

    }

    @Test
    fun `test get personal data`() {

        val identifier = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
                with(ClientDtoMaker.gender, GenderDtoBuilder.maker()
                    .but(with(GenderDtoMaker.genderCode, "OTHER"),
                        with(GenderDtoMaker.externalValue, "Other"))
                    .make()),
                with(ClientDtoMaker.height, 1.76f),
                with(ClientDtoMaker.weight, 80.1f),
                with(ClientDtoMaker.measureType, MeasureTypeDto.KG_CM))
            .make()

        every { customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) } returns clientDto

        val response = classToTest.getPersonalData(identifier)

        then(response.weight).isEqualTo(80.1f)
        then(response.height).isEqualTo(1.76f)
        then(response.gender?.code).isEqualTo("OTHER")
        then(response.gender?.value).isEqualTo("Other")
        then(response.measureType.code).isEqualTo("KG_CM")
        then(response.measureType.value).isEqualTo("Kg/cm")

    }

    @Test
    fun `test update personal data`() {

        val identifier = UUID.randomUUID()
        val request = PersonalDataRequestBuilder.maker()
            .but(with(PersonalDataRequestMaker.genderCode, "M"))
            .make()
        val genderDto = GenderDtoBuilder.maker()
            .but(with(GenderDtoMaker.genderCode, "M"),
                with(GenderDtoMaker.externalValue, "Male"))
            .make()
        val client = slot<ClientDto>()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.identifier, identifier),
                with(ClientDtoMaker.coach, CoachDtoBuilder.default()),
                with(ClientDtoMaker.gender, GenderDtoBuilder.default()),
                with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
                with(ClientDtoMaker.gender, genderDto))
            .make()

        every { customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) } returns clientDto
        every { configsService.getGender("M") } returns genderDto
        every { customerService.updateCustomer(identifier, capture(client)) } answers { client.captured }

        val response = classToTest.updatePersonalData(identifier, request)

        then(response.height).isEqualTo(client.captured.height)
        then(response.weight).isEqualTo(client.captured.weight)
        then(response.gender?.code).isEqualTo(client.captured.gender?.genderCode)
        then(response.gender?.value).isEqualTo(client.captured.gender?.externalValue)
        then(response.measureType.code).isEqualTo(client.captured.measureType.code)
        then(response.measureType.value).isEqualTo(client.captured.measureType.value)

        then(client.captured.firstName).isEqualTo(clientDto.firstName)
        then(client.captured.lastName).isEqualTo(clientDto.lastName)
        then(client.captured.identifier).isEqualTo(clientDto.identifier)
        then(client.captured.phoneCode).isEqualTo(clientDto.phoneNumber)
        then(client.captured.phoneNumber).isEqualTo(clientDto.phoneCode)
        then(client.captured.country?.countryCode).isEqualTo(clientDto.country?.countryCode)
        then(client.captured.country?.externalValue).isEqualTo(clientDto.country?.externalValue)
        then(client.captured.birthday).isEqualTo(clientDto.birthday)
        then(client.captured.coach).isEqualTo(clientDto.coach)
        then(client.captured.loginInfo).isEqualTo(clientDto.loginInfo)
        then(client.captured.customerType).isEqualTo(clientDto.customerType)
        then(client.captured.registrationDate).isEqualTo(clientDto.registrationDate)
        then(client.captured.clientStatus).isEqualTo(clientDto.clientStatus)

    }

    @Test
    fun `test update personal data but measure type is an invalid type`() {

        val identifier = UUID.randomUUID()
        val request = PersonalDataRequestBuilder.maker()
            .but(with(PersonalDataRequestMaker.genderCode, "M"),
                with(PersonalDataRequestMaker.measureTypeCode, "INVALID"))
            .make()
        val genderDto = GenderDtoBuilder.maker()
            .but(with(GenderDtoMaker.genderCode, "M"),
                with(GenderDtoMaker.externalValue, "Male"))
            .make()
        val client = slot<ClientDto>()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.identifier, identifier),
                with(ClientDtoMaker.coach, CoachDtoBuilder.default()),
                with(ClientDtoMaker.gender, GenderDtoBuilder.default()),
                with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
                with(ClientDtoMaker.gender, genderDto))
            .make()

        every { customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) } returns clientDto
        every { configsService.getGender("M") } returns genderDto
        every { customerService.updateCustomer(identifier, capture(client)) } answers { client.captured }

        val response = catchThrowable { classToTest.updatePersonalData(identifier, request) }

        then(response)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("measureTypeCode invalid")

    }

    @Test
    fun `test update personal data but gender is null`() {

        val identifier = UUID.randomUUID()
        val request = PersonalDataRequestBuilder.maker()
            .but(with(PersonalDataRequestMaker.genderCode, null as String?))
            .make()
        val client = slot<ClientDto>()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.gender, null as GenderDto?))
            .make()

        every { customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) } returns clientDto
        every { customerService.updateCustomer(identifier, capture(client)) } answers { client.captured }

        val response = classToTest.updatePersonalData(identifier, request)

        verify(exactly = 0) { configsService.getGender(any()) }

        then(response.gender).isNull()

    }

    @Test
    fun `test update contact info but country is null`() {

        val identifier = UUID.randomUUID()
        val request = ContactInfoRequestBuilder.maker()
            .but(with(ContactInfoRequestMaker.countryCode, null as String?))
            .make()
        val client = slot<ClientDto>()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.country, null as CountryDto?))
            .make()

        every { customerService.getCustomer(identifier, CustomerTypeDto.CLIENT) } returns clientDto
        every { customerService.updateCustomer(identifier, capture(client)) } answers { client.captured }

        val response = classToTest.updateContactInformation(identifier, request)

        verify(exactly = 0) { configsService.getCountry(any()) }

        then(response.country).isNull()

    }

    @Test
    fun `test send client registration invite`() {

        val coachUUID = UUID.randomUUID()
        val coachDto = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.identifier, coachUUID))
            .make()
        val inviteInfoDto = InviteInfoDtoBuilder.maker()
            .but(with(InviteInfoDtoMaker.isRegistrationInvite, false),
                with(InviteInfoDtoMaker.registrationLink, "http://localhost:8080"),
                with(InviteInfoDtoMaker.registrationKey, "MY_KEY"),
                with(InviteInfoDtoMaker.sender, coachUUID),
                with(InviteInfoDtoMaker.clientStatus, ClientStatusDto.PENDING))
            .make()

        every { customerService.getCustomer(coachUUID, CustomerTypeDto.COACH) } returns coachDto
        every { inviteComponent.send(coachDto, "client@test.com") } returns inviteInfoDto

        val response = classToTest.registrationInvite(coachUUID, "client@test.com")

        then(response.coachIdentifier).isEqualTo(coachUUID)
        then(response.registrationKey).isEqualTo("MY_KEY")
        then(response.registrationLink).isEqualTo("http://localhost:8080")
        then(response.registrationInvite).isFalse
        then(response.clientStatus).isEqualTo("PENDING")

    }

    @Test
    fun `test send client registration invite without mandatory parameters`() {

        val exception0 = catchThrowable { classToTest.registrationInvite(null, "client@test.com") }
        then(exception0)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("missing required parameter: coachIdentifier")

        val exception1 = catchThrowable { classToTest.registrationInvite(UUID.randomUUID(), null) }
        then(exception1)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("missing required parameter: clientEmail")

        val exception2 = catchThrowable { classToTest.registrationInvite(UUID.randomUUID(), " ") }
        then(exception2)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("empty/blank required parameter: clientEmail")

    }
}
