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
class ClientToClientDtoConverterTest {

    @MockK
    private lateinit var genderConfigToGenderDtoConverter: GenderConfigToGenderDtoConverter

    @MockK
    private lateinit var countryConfigToCountryDtoConverter: CountryConfigToCountryDtoConverter

    @InjectMockKs
    private lateinit var classToTest: ClientToClientDtoConverter

    private lateinit var clientMaker: Maker<Client>
    private lateinit var countryDtoMaker: Maker<CountryDto>
    private lateinit var genderDtoMaker: Maker<GenderDto>

    @BeforeEach
    fun setUp() {
        clientMaker = an(ClientMaker.Client)
        genderDtoMaker = an(GenderDtoMaker.GenderDto)
        countryDtoMaker = an(CountryDtoMaker.CountryDto)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `client convert all values`() {

        // given
        val client = clientMaker
            .but(with(ClientMaker.gender, make(a(GenderMaker.GenderConfig))),
                with(ClientMaker.country, make(a(CountryMaker.CountryConfig))),
                with(ClientMaker.birthday, LocalDate.now()),
                with(ClientMaker.phoneCode, "333"),
                with(ClientMaker.phoneNumber, "222222111111"))
            .make()
        val genderDto = genderDtoMaker.make()
        val countryDto = countryDtoMaker.make()
        every { genderConfigToGenderDtoConverter.convert(any()) } returns genderDto
        every { countryConfigToCountryDtoConverter.convert(any()) } returns countryDto

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 1) { genderConfigToGenderDtoConverter.convert(client.gender!!) }
        verify(exactly = 1) { countryConfigToCountryDtoConverter.convert(client.country!!) }
        then(clientDto.identifier).isEqualTo(client.uuid)
        then(clientDto.firstName).isEqualTo(client.firstName)
        then(clientDto.lastName).isEqualTo(client.lastName)
        then(clientDto.birthday).isEqualTo(client.birthday)
        then(clientDto.phoneCode).isEqualTo(client.phoneCode)
        then(clientDto.phoneNumber).isEqualTo(client.phoneNumber)
        then(clientDto.country).isEqualTo(countryDto)
        then(clientDto.gender).isEqualTo(genderDto)
        then(clientDto.registrationDate).isNotNull
        then(clientDto.customerType).isEqualTo(CustomerTypeDto.CLIENT)
    }

    @Test
    fun `client convert without country`() {

        // given
        val client = clientMaker
            .but(with(ClientMaker.country, null as CountryConfig?),
                with(ClientMaker.gender, make(a(GenderMaker.GenderConfig))))
            .make()
        val genderDto = genderDtoMaker.make()
        every { genderConfigToGenderDtoConverter.convert(any()) } returns genderDto
        check(client.country === null)

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 1) { genderConfigToGenderDtoConverter.convert(any()) }
        verify(exactly = 0) { countryConfigToCountryDtoConverter.convert(any()) }
        then(clientDto).isNotNull
        then(clientDto.country).isNull()
    }

    @Test
    fun `client convert without gender`() {

        // given
        val client = clientMaker
            .but(with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.country, make(a(CountryMaker.CountryConfig))))
            .make()
        val countryDto = countryDtoMaker.make()
        every { countryConfigToCountryDtoConverter.convert(any()) } returns countryDto
        check(client.gender === null)

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 0) { genderConfigToGenderDtoConverter.convert(any()) }
        verify(exactly = 1) { countryConfigToCountryDtoConverter.convert(any()) }
        then(clientDto).isNotNull
        then(clientDto.gender).isNull()
    }

}