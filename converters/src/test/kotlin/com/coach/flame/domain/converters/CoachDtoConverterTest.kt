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
class CoachDtoConverterTest {

    @SpyK
    private var genderDtoConverter: GenderDtoConverter = GenderDtoConverter()

    @SpyK
    private var countryDtoConverter: CountryDtoConverter = CountryDtoConverter()

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
        val coachDto = classToTest.convert(coach)

        //then
        verify(exactly = 3) { genderDtoConverter.convert(any()) }
        verify(exactly = 3) { countryDtoConverter.convert(any()) }
        then(coachDto.identifier).isEqualTo(coach.uuid)
        then(coachDto.firstName).isEqualTo(coach.firstName)
        then(coachDto.lastName).isEqualTo(coach.lastName)
        then(coachDto.birthday).isEqualTo(coach.birthday)
        then(coachDto.phoneCode).isEqualTo(coach.phoneCode)
        then(coachDto.phoneNumber).isEqualTo(coach.phoneNumber)
        then(coachDto.country).isEqualTo(countryDto)
        then(coachDto.gender).isEqualTo(genderDto)
        then(coachDto.registrationDate).isNotNull
        then(coachDto.customerType).isEqualTo(CustomerTypeDto.COACH)

        // Clients
        then(coachDto.listOfClients).hasSize(2)

        val filterClient0 = coachDto.listOfClients.find { it.identifier == client0.uuid }
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
        then(filterClient0?.height).isEqualTo(0.0f)
        then(filterClient0?.weight).isEqualTo(0.0f)
        then(filterClient0?.measureType).isEqualTo(MeasureTypeDto.KG_CM)

    }

    @Test
    fun `coach convert without country`() {

        // given
        val client = CoachBuilder.maker()
            .but(with(CoachMaker.country, null as CountryConfig?),
                with(CoachMaker.gender, make(a(GenderMaker.GenderConfig))))
            .make()
        val genderDto = GenderDtoBuilder.default()
        every { genderDtoConverter.convert(any()) } returns genderDto
        check(client.country === null)

        // when
        val coachDto = classToTest.convert(client)

        //then
        verify(exactly = 1) { genderDtoConverter.convert(any()) }
        verify(exactly = 0) { countryDtoConverter.convert(any()) }
        then(coachDto).isNotNull
        then(coachDto.country).isNull()
    }

    @Test
    fun `coach convert without gender`() {

        // given
        val client = CoachBuilder.maker()
            .but(with(CoachMaker.gender, null as GenderConfig?),
                with(CoachMaker.country, make(a(CountryMaker.CountryConfig))))
            .make()
        val countryDto = CountryDtoBuilder.default()
        every { countryDtoConverter.convert(any()) } returns countryDto
        check(client.gender === null)

        // when
        val coachDto = classToTest.convert(client)

        //then
        verify(exactly = 0) { genderDtoConverter.convert(any()) }
        verify(exactly = 1) { countryDtoConverter.convert(any()) }
        then(coachDto).isNotNull
        then(coachDto.gender).isNull()
    }

    @Test
    fun `coach convert with invalid customer type`() {

        // given
        val client = CoachBuilder.maker()
            .but(with(CoachMaker.gender, GenderBuilder.default()),
                with(CoachMaker.country, CountryBuilder.default()),
                with(CoachMaker.clientType, ClientTypeBuilder.maker()
                    .but(with(ClientTypeMaker.type, "OTHER_TYPE"))
                    .make()))
            .make()
        every { countryDtoConverter.convert(any()) } returns mockk()
        every { countryDtoConverter.convert(any()) } returns mockk()

        // when
        val coachDto = classToTest.convert(client)

        //then
        verify(exactly = 1) { genderDtoConverter.convert(any()) }
        verify(exactly = 1) { countryDtoConverter.convert(any()) }
        then(coachDto).isNotNull
        then(coachDto.customerType).isEqualTo(CustomerTypeDto.UNKNOWN)
    }

}
