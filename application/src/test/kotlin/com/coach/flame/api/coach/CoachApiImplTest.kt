package com.coach.flame.api.coach

import com.coach.flame.api.coach.request.ContactInfoRequestBuilder
import com.coach.flame.api.coach.request.ContactInfoRequestMaker
import com.coach.flame.configs.ConfigsService
import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.coach.CoachService
import com.coach.flame.domain.*
import com.coach.flame.exception.RestException
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
class CoachApiImplTest {

    @MockK
    private lateinit var coachService: CoachService

    @MockK
    private lateinit var customerService: CustomerService

    @MockK
    private lateinit var configsService: ConfigsService

    @InjectMockKs
    private lateinit var classToTest: CoachApiImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get clients from a coach`() {

        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")

        val client0 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default())).make()
        val client1 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default())).make()
        val clients = setOf(client0, client1)
        val clientCoach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
                with(CoachDtoMaker.customerType, CustomerTypeDto.COACH),
                with(CoachDtoMaker.identifier, uuid),
                with(CoachDtoMaker.listOfClients, clients))
            .make()

        every { coachService.getCoachWithClientsAccepted(uuid) } returns clientCoach

        val response = classToTest.getClientsCoach(uuid.toString())

        then(response.identifier).isEqualTo(uuid)
        then(response.clientsCoach).isNotEmpty
        then(response.clientsCoach).hasSize(2)

    }

    @Test
    fun `test get clients from a coach with unexpected error`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        every { coachService.getCoachWithClientsAccepted(uuid) } throws RuntimeException("Something wrong happened")

        // when
        val thrown = catchThrowable { classToTest.getClientsCoach(uuid.toString()) }

        //then
        then(thrown)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Something wrong happened")
    }

    @Test
    fun `test get clients coach plus clients available for a coach`() {

        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")

        val client0 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default())).make()
        val client1 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default())).make()
        val client2 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
            with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED)).make()
        val client3 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
            with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED)).make()
        val client4 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
            with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING)).make()
        val coach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.listOfClients, setOf(client0, client1, client2, client3, client4)),
                with(CoachDtoMaker.identifier, uuid)).make()

        every { coachService.getCoachWithClientsAvailable(uuid) } returns coach

        val response = classToTest.getClientsCoachPlusClientsAvailable(uuid.toString())

        then(response.identifier).isEqualTo(uuid)
        then(response.clientsCoach).isNotEmpty
        then(response.clientsCoach).hasSize(5)
        then(response.clientsCoach.filter { ClientStatusDto.ACCEPTED.name == it.status }).hasSize(2)
        then(response.clientsCoach.filter { ClientStatusDto.PENDING.name == it.status }).hasSize(1)
        then(response.clientsCoach.filter { ClientStatusDto.AVAILABLE.name == it.status }).hasSize(2)

    }

    @Test
    fun `test get clients for a coach invalid uuid format`() {

        // given
        val uuid = "INVALID"

        // when
        val thrown = catchThrowable { classToTest.getClientsCoach(uuid) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("Invalid UUID string: INVALID")
    }

    @Test
    fun `test get clients coach plus clients available for a coach invalid uuid format`() {

        // given
        val uuid = "INVALID"

        // when
        val thrown = catchThrowable { classToTest.getClientsCoach(uuid) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("Invalid UUID string: INVALID")
    }

    @Test
    fun `test get contact info`() {

        val identifier = UUID.randomUUID()
        val coach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
                with(CoachDtoMaker.country, CountryDtoBuilder.default()))
            .make()
        every { customerService.getCustomer(identifier, CustomerTypeDto.COACH) } returns coach

        val response = classToTest.getContactInformation(identifier)

        then(response.firstName).isEqualTo(coach.firstName)
        then(response.lastName).isEqualTo(coach.lastName)
        then(response.identifier).isEqualTo(coach.identifier)
        then(response.phoneCode).isEqualTo(coach.phoneCode)
        then(response.phoneNumber).isEqualTo(coach.phoneNumber)
        then(response.country?.code).isEqualTo(coach.country?.countryCode)
        then(response.country?.value).isEqualTo(coach.country?.externalValue)

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
        val coach = slot<CoachDto>()
        val coachDto = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.identifier, identifier),
                with(CoachDtoMaker.gender, GenderDtoBuilder.default()),
                with(CoachDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
                with(CoachDtoMaker.country, countryDto))
            .make()

        every { customerService.getCustomer(identifier, CustomerTypeDto.COACH) } returns coachDto
        every { configsService.getCountry("PT") } returns countryDto
        every { customerService.updateCustomer(identifier, capture(coach)) } answers { coach.captured }

        val response = classToTest.updateContactInformation(identifier, request)

        then(response.firstName).isEqualTo(request.firstName)
        then(response.lastName).isEqualTo(response.lastName)
        then(response.identifier).isEqualTo(identifier)
        then(response.phoneCode).isEqualTo(request.phoneCode)
        then(response.phoneNumber).isEqualTo(request.phoneNumber)
        then(response.country?.code).isEqualTo(request.countryCode)
        then(response.country?.value).isEqualTo("Portugal")

        then(coach.captured.birthday).isEqualTo(coachDto.birthday)
        then(coach.captured.listOfClients).isEqualTo(coachDto.listOfClients)
        then(coach.captured.loginInfo).isEqualTo(coachDto.loginInfo)
        then(coach.captured.gender).isEqualTo(coachDto.gender)
        then(coach.captured.customerType).isEqualTo(coachDto.customerType)
        then(coach.captured.registrationDate).isEqualTo(coachDto.registrationDate)

    }

}
