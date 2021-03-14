package com.coach.flame.domain.converters

import com.coach.flame.domain.*
import com.coach.flame.jpa.entity.*
import com.natpryce.makeiteasy.MakeItEasy.*
import com.natpryce.makeiteasy.Maker
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class CoachToCoachDtoConverterTest {

    @MockK
    private lateinit var genderConfigToGenderDtoConverter: GenderConfigToGenderDtoConverter

    @MockK
    private lateinit var countryConfigToCountryDtoConverter: CountryConfigToCountryDtoConverter

    @MockK
    private lateinit var clientToClientDtoConverter: ClientToClientDtoConverter

    @InjectMockKs
    private lateinit var classToTest: CoachToCoachDtoConverter

    private lateinit var coachMaker: Maker<Coach>
    private lateinit var clientMaker: Maker<Client>
    private lateinit var clientDtoMaker: Maker<ClientDto>
    private lateinit var countryDtoMaker: Maker<CountryDto>
    private lateinit var genderDtoMaker: Maker<GenderDto>

    @BeforeEach
    fun setUp() {
        coachMaker = an(CoachMaker.Coach)
        clientMaker = an(ClientMaker.Client)
        clientDtoMaker = an(ClientDtoMaker.ClientDto)
        genderDtoMaker = an(GenderDtoMaker.GenderDto)
        countryDtoMaker = an(CountryDtoMaker.CountryDto)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `coach convert all values`() {

        // given
        val clients = mutableListOf(clientMaker.make(), clientMaker.make(), clientMaker.make())
        val coach = coachMaker
            .but(with(CoachMaker.gender, make(a(GenderMaker.GenderConfig))),
                with(CoachMaker.country, make(a(CountryMaker.CountryConfig))),
                with(CoachMaker.birthday, LocalDate.now()),
                with(CoachMaker.phoneCode, "333"),
                with(CoachMaker.phoneNumber, "222222111111"),
                with(CoachMaker.clients, clients))
            .make()
        val genderDto = genderDtoMaker.make()
        val countryDto = countryDtoMaker.make()
        every { genderConfigToGenderDtoConverter.convert(any()) } returns genderDto
        every { countryConfigToCountryDtoConverter.convert(any()) } returns countryDto
        every { clientToClientDtoConverter.convert(clients[0]) } returns clientDtoMaker.make()
        every { clientToClientDtoConverter.convert(clients[1]) } returns clientDtoMaker.make()
        every { clientToClientDtoConverter.convert(clients[2]) } returns clientDtoMaker.make()

        // when
        val clientDto = classToTest.convert(coach)

        //then
        verify(exactly = 1) { genderConfigToGenderDtoConverter.convert(coach.gender!!) }
        verify(exactly = 1) { countryConfigToCountryDtoConverter.convert(coach.country!!) }
        verify(exactly = 3) { clientToClientDtoConverter.convert(any()) }
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
        then(clientDto.listOfClients).hasSize(3)
    }

    @Test
    fun `client convert without country`() {

        // given
        val client = coachMaker
            .but(with(CoachMaker.country, null as CountryConfig?),
                with(CoachMaker.gender, make(a(GenderMaker.GenderConfig))))
            .make()
        val genderDto = genderDtoMaker.make()
        every { genderConfigToGenderDtoConverter.convert(any()) } returns genderDto
        check(client.country === null)

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 1) { genderConfigToGenderDtoConverter.convert(any()) }
        verify(exactly = 0) { countryConfigToCountryDtoConverter.convert(any()) }
        verify(exactly = 0) { clientToClientDtoConverter.convert(any()) }
        then(clientDto).isNotNull
        then(clientDto.country).isNull()
    }

    @Test
    fun `client convert without gender`() {

        // given
        val client = coachMaker
            .but(with(CoachMaker.gender, null as GenderConfig?),
                with(CoachMaker.country, make(a(CountryMaker.CountryConfig))))
            .make()
        val countryDto = countryDtoMaker.make()
        every { countryConfigToCountryDtoConverter.convert(any()) } returns countryDto
        check(client.gender === null)

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 0) { genderConfigToGenderDtoConverter.convert(any()) }
        verify(exactly = 1) { countryConfigToCountryDtoConverter.convert(any()) }
        verify(exactly = 0) { clientToClientDtoConverter.convert(any()) }
        then(clientDto).isNotNull
        then(clientDto.gender).isNull()
    }

}