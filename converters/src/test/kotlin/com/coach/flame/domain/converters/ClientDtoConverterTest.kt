package com.coach.flame.domain.converters

import com.coach.flame.domain.ClientTypeDto
import com.coach.flame.domain.CountryDtoGenerator
import com.coach.flame.domain.GenderDtoGenerator
import com.coach.flame.jpa.entity.ClientGenerator
import com.coach.flame.jpa.entity.ClientType
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

    private val countryDtoGenerator = CountryDtoGenerator.Builder().build()
    private val genderDtoGenerator = GenderDtoGenerator.Builder().build()

    @AfterEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `client convert all values`() {

        // given
        val client = ClientGenerator.Builder()
            .withRandomizerClientType { ClientType(type = "client", mutableListOf()) }
            .build()
            .nextObject()
        val genderDto = genderDtoGenerator.nextObject()
        val countryDto = countryDtoGenerator.nextObject()
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
        val client = ClientGenerator.Builder()
            .withRandomizerCountry { null }
            .build()
            .nextObject()
        val genderDto = genderDtoGenerator.nextObject()
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
        val client = ClientGenerator.Builder()
            .withRandomizerGender { null }
            .build()
            .nextObject()
        val countryDto = countryDtoGenerator.nextObject()
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