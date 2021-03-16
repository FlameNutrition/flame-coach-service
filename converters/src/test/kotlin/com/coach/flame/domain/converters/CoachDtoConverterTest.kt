package com.coach.flame.domain.converters

import com.coach.flame.domain.*
import com.coach.flame.jpa.entity.*
import com.natpryce.makeiteasy.MakeItEasy.*
import com.natpryce.makeiteasy.Maker
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class CoachDtoConverterTest {

    @SpyK
    private var genderDtoConverter: GenderDtoConverter = GenderDtoConverter()

    @SpyK
    private var countryDtoConverter: CountryDtoConverter = CountryDtoConverter()

    @MockK
    private lateinit var clientDtoConverter: ClientDtoConverter

    @InjectMockKs
    private lateinit var classToTest: CoachDtoConverter

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `coach convert all values`() {

        // given
        val clientsMaker = ClientBuilder.maker()
        val client0 = clientsMaker
            .but(with(ClientMaker.gender, make(a(GenderMaker.GenderConfig))),
                with(ClientMaker.country, make(a(CountryMaker.CountryConfig))),
                with(ClientMaker.birthday, LocalDate.now()),
                with(ClientMaker.phoneCode, "333"),
                with(ClientMaker.phoneNumber, "1111111"),
                with(ClientMaker.coach, CoachBuilder.default()))
            .make()
        val client1 = clientsMaker
            .but(with(ClientMaker.gender, make(a(GenderMaker.GenderConfig))),
                with(ClientMaker.country, make(a(CountryMaker.CountryConfig))),
                with(ClientMaker.birthday, LocalDate.now()),
                with(ClientMaker.phoneCode, "444"),
                with(ClientMaker.phoneNumber, "2222222"),
                with(ClientMaker.coach, CoachBuilder.default()))
            .make()
        val clients = mutableListOf(client0, client1)
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.gender, make(a(GenderMaker.GenderConfig))),
                with(CoachMaker.country, make(a(CountryMaker.CountryConfig))),
                with(CoachMaker.birthday, LocalDate.now()),
                with(CoachMaker.phoneCode, "333"),
                with(CoachMaker.phoneNumber, "222222111111"),
                with(CoachMaker.clients, clients))
            .make()
        val genderDto = GenderDtoBuilder.default()
        val countryDto = CountryDtoBuilder.default()
        every { genderDtoConverter.convert(any()) } returns genderDto
        every { countryDtoConverter.convert(any()) } returns countryDto

        // when
        val clientDto = classToTest.convert(coach)

        //then
        verify(exactly = 3) { genderDtoConverter.convert(any()) }
        verify(exactly = 3) { countryDtoConverter.convert(any()) }
        then(clientDto.identifier).isEqualTo(coach.uuid)
        then(clientDto.firstName).isEqualTo(coach.firstName)
        then(clientDto.lastName).isEqualTo(coach.lastName)
        then(clientDto.birthday).isEqualTo(coach.birthday)
        then(clientDto.phoneCode).isEqualTo(coach.phoneCode)
        then(clientDto.phoneNumber).isEqualTo(coach.phoneNumber)
        then(clientDto.country).isEqualTo(countryDto)
        then(clientDto.gender).isEqualTo(genderDto)
        then(clientDto.registrationDate).isNotNull
        then(clientDto.customerType).isEqualTo(CustomerTypeDto.COACH)

        // Clients
        then(clientDto.listOfClients).hasSize(2)

        val filterClient0 = clientDto.listOfClients.find { it.identifier == client0.uuid }
        then(filterClient0).isNotNull
        then(filterClient0?.firstName).isEqualTo(client0.firstName)
        then(filterClient0?.lastName).isEqualTo(client0.lastName)
        then(filterClient0?.birthday).isEqualTo(client0.birthday)
        then(filterClient0?.phoneCode).isEqualTo(client0.phoneCode)
        then(filterClient0?.phoneNumber).isEqualTo(client0.phoneNumber)
        then(filterClient0?.country).isEqualTo(countryDto)
        then(filterClient0?.gender).isEqualTo(genderDto)
        then(filterClient0?.registrationDate).isEqualTo(client0.registrationDate)
        then(filterClient0?.customerType).isEqualTo(CustomerTypeDto.CLIENT)
        then(filterClient0?.loginInfo).isNotNull
        then(filterClient0?.loginInfo?.username).isEqualTo(client0.user.email)
        then(filterClient0?.loginInfo?.password).isEqualTo("******")
        then(filterClient0?.coach).isNull()

    }

    @Test
    fun `client convert without country`() {

        // given
        val client = CoachBuilder.maker()
            .but(with(CoachMaker.country, null as CountryConfig?),
                with(CoachMaker.gender, make(a(GenderMaker.GenderConfig))))
            .make()
        val genderDto = GenderDtoBuilder.default()
        every { genderDtoConverter.convert(any()) } returns genderDto
        check(client.country === null)

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 1) { genderDtoConverter.convert(any()) }
        verify(exactly = 0) { countryDtoConverter.convert(any()) }
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        then(clientDto).isNotNull
        then(clientDto.country).isNull()
    }

    @Test
    fun `client convert without gender`() {

        // given
        val client = CoachBuilder.maker()
            .but(with(CoachMaker.gender, null as GenderConfig?),
                with(CoachMaker.country, make(a(CountryMaker.CountryConfig))))
            .make()
        val countryDto = CountryDtoBuilder.default()
        every { countryDtoConverter.convert(any()) } returns countryDto
        check(client.gender === null)

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 0) { genderDtoConverter.convert(any()) }
        verify(exactly = 1) { countryDtoConverter.convert(any()) }
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        then(clientDto).isNotNull
        then(clientDto.gender).isNull()
    }

}