package com.coach.flame.domain.converters

import com.coach.flame.domain.*
import com.coach.flame.jpa.entity.*
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.MakeItEasy.with
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

@ExtendWith(MockKExtension::class)
class ClientDtoConverterTest {

    @MockK
    private lateinit var genderDtoConverter: GenderDtoConverter

    @MockK
    private lateinit var countryDtoConverter: CountryDtoConverter

    @InjectMockKs
    private lateinit var classToTest: ClientDtoConverter

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
        val client = clientMaker.make()
        val genderDto = genderDtoMaker.make()
        val countryDto = countryDtoMaker.make()
        every { genderDtoConverter.convert(any()) } returns genderDto
        every { countryDtoConverter.convert(any()) } returns countryDto

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 1) { genderDtoConverter.convert(client.gender!!) }
        verify(exactly = 1) { countryDtoConverter.convert(client.country!!) }
        then(clientDto.identifier).isEqualTo(client.uuid)
        then(clientDto.firstName).isEqualTo(client.firstName)
        then(clientDto.lastName).isEqualTo(client.lastName)
        then(clientDto.birthday).isEqualTo(client.birthday)
        then(clientDto.phoneCode).isEqualTo(client.phoneCode)
        then(clientDto.phoneNumber).isEqualTo(client.phoneNumber)
        then(clientDto.country).isEqualTo(countryDto)
        then(clientDto.gender).isEqualTo(genderDto)
        then(clientDto.clientType).isEqualTo(ClientTypeDto.CLIENT)
    }

    @Test
    fun `client convert without country`() {

        // given
        val client = clientMaker
            .but(with(ClientMaker.country, null as CountryConfig?))
            .make()
        val genderDto = genderDtoMaker.make()
        every { genderDtoConverter.convert(any()) } returns genderDto
        check(client.country === null)

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 1) { genderDtoConverter.convert(any()) }
        verify(exactly = 0) { countryDtoConverter.convert(any()) }
        then(clientDto).isNotNull
        then(clientDto.country).isNull()
    }

    @Test
    fun `client convert without gender`() {

        // given
        val client = clientMaker
            .but(with(ClientMaker.gender, null as GenderConfig?))
            .make()
        val countryDto = countryDtoMaker.make()
        every { countryDtoConverter.convert(any()) } returns countryDto
        check(client.gender === null)

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 0) { genderDtoConverter.convert(any()) }
        verify(exactly = 1) { countryDtoConverter.convert(any()) }
        then(clientDto).isNotNull
        then(clientDto.gender).isNull()
    }


}