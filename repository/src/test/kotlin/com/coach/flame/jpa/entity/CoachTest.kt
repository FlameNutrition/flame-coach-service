package com.coach.flame.jpa.entity

import com.coach.flame.domain.*
import com.coach.flame.domain.maker.*
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import com.coach.flame.jpa.entity.maker.*
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.util.*

class CoachTest {

    @Test
    fun `test convert coach to dto with null values`() {

        val client0 = ClientBuilder.default()
        val client1 = ClientBuilder.default()

        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.clients, listOf(client0, client1)))
            .make()

        val dto = coach.toDto()

        then(dto.id).isEqualTo(coach.id)
        then(dto.identifier).isEqualTo(coach.uuid)
        then(dto.firstName).isEqualTo(coach.firstName)
        then(dto.lastName).isEqualTo(coach.lastName)
        then(dto.birthday).isEqualTo(coach.birthday)
        then(dto.phoneCode).isEqualTo(coach.phoneCode)
        then(dto.phoneNumber).isEqualTo(coach.phoneNumber)
        then(dto.country).isNull()
        then(dto.gender).isNull()
        then(dto.customerType).isEqualTo(CustomerTypeDto.COACH)
        then(dto.loginInfo).isNotNull
        then(dto.registrationDate).isEqualTo(coach.registrationDate)
        then(dto.listOfClients).isNotNull
        then(dto.listOfClients).hasSize(2)

    }

    @Test
    fun `test convert coach to dto all values`() {

        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.country, CountryBuilder.default()),
                with(CoachMaker.gender, GenderBuilder.default()))
            .make()

        val dto = coach.toDto()

        then(dto.id).isEqualTo(coach.id)
        then(dto.country).isNotNull
        then(dto.gender).isNotNull
    }

    @Test
    fun `test convert dto to entity with null values`() {

        val loginInfoDto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
            .make()

        val coachDto = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.loginInfo, loginInfoDto))
            .make()

        val entity = coachDto.toCoach()

        then(entity.id).isEqualTo(coachDto.id)
        then(entity.uuid).isEqualTo(coachDto.identifier)
        then(entity.firstName).isEqualTo(coachDto.firstName)
        then(entity.lastName).isEqualTo(coachDto.lastName)
        then(entity.birthday).isEqualTo(coachDto.birthday)
        then(entity.phoneCode).isEqualTo(coachDto.phoneCode)
        then(entity.phoneNumber).isEqualTo(coachDto.phoneNumber)
        then(entity.country).isNull()
        then(entity.gender).isNull()
        then(entity.clientType).isNotNull
        then(entity.user).isNotNull
        then(entity.registrationDate).isEqualTo(coachDto.registrationDate)
    }

    @Test
    fun `test convert dto to entity all values`() {

        val loginInfoDto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
            .make()

        val coachDto = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.id, 100L),
                with(CoachDtoMaker.loginInfo, loginInfoDto),
                with(CoachDtoMaker.country, CountryDtoBuilder.default()),
                with(CoachDtoMaker.gender, GenderDtoBuilder.default()))
            .make()

        val entity = coachDto.toCoach()

        then(entity.id).isEqualTo(coachDto.id)
        then(entity.country).isNotNull
        then(entity.gender).isNotNull
    }

    @Test
    fun `test convert dto to entity illegal args`() {

        val clientDtoLoginInfoNull = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.loginInfo, null as LoginInfoDto?))
            .make()

        val exception0 = catchThrowable { clientDtoLoginInfoNull.toClient() }

        then(exception0).isInstanceOf(IllegalArgumentException::class.java)
        then(exception0).hasMessageContaining("loginInfo can not be null")
    }

}
