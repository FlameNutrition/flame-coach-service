package com.coach.flame.domain.converters

import com.coach.flame.domain.CountryDtoBuilder
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.GenderDtoBuilder
import com.coach.flame.domain.MeasureTypeDto
import com.coach.flame.jpa.entity.*
import com.natpryce.makeiteasy.MakeItEasy.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class ClientDtoConverterTest {

    @SpyK
    private var genderDtoConverter: GenderDtoConverter = GenderDtoConverter()

    @SpyK
    private var countryDtoConverter: CountryDtoConverter = CountryDtoConverter()

    @InjectMockKs
    private lateinit var classToTest: ClientDtoConverter

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `client convert all values`() {

        // given
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.gender, make(a(GenderMaker.GenderConfig))),
                with(CoachMaker.country, make(a(CountryMaker.CountryConfig))),
                with(CoachMaker.birthday, LocalDate.now()),
                with(CoachMaker.phoneCode, "4000"),
                with(CoachMaker.phoneNumber, "2222"),
                with(CoachMaker.user, UserBuilder.default()),
                with(CoachMaker.clients, listOf(ClientBuilder.default(), ClientBuilder.default())))
            .make()
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.gender, make(a(GenderMaker.GenderConfig))),
                with(ClientMaker.country, make(a(CountryMaker.CountryConfig))),
                with(ClientMaker.birthday, LocalDate.now()),
                with(ClientMaker.phoneCode, "333"),
                with(ClientMaker.coach, coach),
                with(ClientMaker.phoneNumber, "222222111111"))
            .make()
        val genderDto = GenderDtoBuilder.default()
        val genderCoachDto = GenderDtoBuilder.default()
        val countryDto = CountryDtoBuilder.default()
        val countryCoachDto = CountryDtoBuilder.default()
        every { genderDtoConverter.convert(client.gender!!) } returns genderDto
        every { genderDtoConverter.convert(coach.gender!!) } returns genderCoachDto
        every { countryDtoConverter.convert(client.country!!) } returns countryDto
        every { countryDtoConverter.convert(coach.country!!) } returns countryCoachDto

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
        then(clientDto.registrationDate).isNotNull
        then(clientDto.customerType).isEqualTo(CustomerTypeDto.CLIENT)
        then(clientDto.weight).isEqualTo(0.0f)
        then(clientDto.height).isEqualTo(0.0f)
        then(clientDto.measureType).isEqualTo(MeasureTypeDto.KG_CM)

        //Coach
        verify(exactly = 1) { genderDtoConverter.convert(client.coach?.gender!!) }
        verify(exactly = 1) { countryDtoConverter.convert(client.coach?.country!!) }
        then(clientDto.coach).isNotNull
        then(clientDto.coach?.identifier).isEqualTo(coach.uuid)
        then(clientDto.coach?.firstName).isEqualTo(coach.firstName)
        then(clientDto.coach?.lastName).isEqualTo(coach.lastName)
        then(clientDto.coach?.birthday).isEqualTo(coach.birthday)
        then(clientDto.coach?.phoneCode).isEqualTo(coach.phoneCode)
        then(clientDto.coach?.phoneNumber).isEqualTo(coach.phoneNumber)
        then(clientDto.coach?.country).isEqualTo(countryCoachDto)
        then(clientDto.coach?.gender).isEqualTo(genderCoachDto)
        then(clientDto.coach?.registrationDate).isEqualTo(coach.registrationDate)
        then(clientDto.coach?.customerType).isEqualTo(CustomerTypeDto.COACH)
        then(clientDto.coach?.loginInfo).isNull()
        then(clientDto.coach?.listOfClients).isEmpty()

    }

    @Test
    fun `client convert when coach is null`() {

        // given
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.gender, make(a(GenderMaker.GenderConfig))),
                with(ClientMaker.country, make(a(CountryMaker.CountryConfig))),
                with(ClientMaker.birthday, LocalDate.now()),
                with(ClientMaker.phoneCode, "333"),
                with(ClientMaker.phoneNumber, "222222111111"))
            .make()
        val genderDto = GenderDtoBuilder.default()
        val countryDto = CountryDtoBuilder.default()
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
        then(clientDto.registrationDate).isNotNull
        then(clientDto.coach).isNull()
        then(clientDto.customerType).isEqualTo(CustomerTypeDto.CLIENT)
    }

    @Test
    fun `client convert without country`() {

        // given
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.country, null as CountryConfig?),
                with(ClientMaker.gender, make(a(GenderMaker.GenderConfig))))
            .make()
        val genderDto = GenderDtoBuilder.default()
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
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.country, make(a(CountryMaker.CountryConfig))))
            .make()
        val countryDto = CountryDtoBuilder.default()
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

    @Test
    fun `client convert with invalid customer type`() {

        // given
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.gender, GenderBuilder.default()),
                with(ClientMaker.country, CountryBuilder.default()),
                with(ClientMaker.clientType, ClientTypeBuilder.maker()
                    .but(with(ClientTypeMaker.type, "OTHER_TYPE"))
                    .make()))
            .make()
        every { countryDtoConverter.convert(any()) } returns mockk()
        every { countryDtoConverter.convert(any()) } returns mockk()

        // when
        val clientDto = classToTest.convert(client)

        //then
        verify(exactly = 1) { genderDtoConverter.convert(any()) }
        verify(exactly = 1) { countryDtoConverter.convert(any()) }
        then(clientDto).isNotNull
        then(clientDto.customerType).isEqualTo(CustomerTypeDto.UNKNOWN)
    }


}
