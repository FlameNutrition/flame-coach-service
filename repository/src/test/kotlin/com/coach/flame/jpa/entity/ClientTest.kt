package com.coach.flame.jpa.entity

import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.LoginInfoDto
import com.coach.flame.domain.maker.*
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.maker.*
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class ClientTest {

    @Test
    fun `test convert client to dto with null values`() {

        val measureWeight1 = ClientMeasureWeightBuilder.default()
        val measureWeight2 = ClientMeasureWeightBuilder.default()

        val client = ClientBuilder.maker()
            .but(with(ClientMaker.clientMeasureWeight,
                mutableListOf(measureWeight1, measureWeight2)))
            .make()

        val dto = client.toDto()

        then(dto.id).isEqualTo(client.id)
        then(dto.identifier).isEqualTo(client.uuid)
        then(dto.firstName).isEqualTo(client.firstName)
        then(dto.lastName).isEqualTo(client.lastName)
        then(dto.birthday).isEqualTo(client.birthday)
        then(dto.phoneCode).isEqualTo(client.phoneCode)
        then(dto.phoneNumber).isEqualTo(client.phoneNumber)
        then(dto.country).isNull()
        then(dto.gender).isNull()
        then(dto.coach).isNull()
        then(dto.weight).isEqualTo(client.weight)
        then(dto.height).isEqualTo(client.height)
        then(dto.measureType.name).isEqualTo(client.measureConfig.name)
        then(dto.loginInfo).isNotNull
        then(dto.customerType.name).isEqualTo(client.clientType.type)
        then(dto.clientStatus?.name).isEqualTo(client.clientStatus.name)
        then(dto.weightMeasureTimeline).isNotNull
        then(dto.weightMeasureTimeline).hasSize(2)

    }

    @Test
    fun `test convert client to dto all values`() {

        val client = ClientBuilder.maker()
            .but(with(ClientMaker.country, CountryBuilder.default()),
                with(ClientMaker.gender, GenderBuilder.default()),
                with(ClientMaker.coach, CoachBuilder.default()))
            .make()

        val dto = client.toDto(client.coach?.toDto())

        then(dto.country).isNotNull
        then(dto.gender).isNotNull
        then(dto.coach).isNotNull
    }

    @Test
    fun `test convert dto to entity with null values`() {

        val loginInfoDto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
            .make()

        val measureWeight1 = MeasureDtoBuilder.default()
        val measureWeight2 = MeasureDtoBuilder.default()

        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.loginInfo, loginInfoDto),
                with(ClientDtoMaker.listOfWeights, mutableListOf(measureWeight1, measureWeight2)))
            .make()

        val entity = clientDto.toClient()

        then(entity.id).isEqualTo(clientDto.id)
        then(entity.uuid).isEqualTo(clientDto.identifier)
        then(entity.firstName).isEqualTo(clientDto.firstName)
        then(entity.lastName).isEqualTo(clientDto.lastName)
        then(entity.birthday).isEqualTo(clientDto.birthday)
        then(entity.phoneCode).isEqualTo(clientDto.phoneCode)
        then(entity.phoneNumber).isEqualTo(clientDto.phoneNumber)
        then(entity.country).isNull()
        then(entity.gender).isNull()
        then(entity.coach).isNull()
        then(entity.weight).isEqualTo(clientDto.weight)
        then(entity.height).isEqualTo(clientDto.height)
        then(entity.measureConfig.name).isEqualTo(clientDto.measureType.name)
        then(entity.user).isNotNull
        then(entity.clientType.type).isEqualTo(clientDto.customerType.name)
        then(entity.clientStatus.name).isEqualTo(clientDto.clientStatus?.name)
        then(entity.clientMeasureWeight).isNotNull
        then(entity.clientMeasureWeight).hasSize(2)

    }

    @Test
    fun `test convert dto to entity all values`() {

        val loginInfoDto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
            .make()

        val loginInfoDtoCoach = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
            .make()

        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.id, 100L),
                with(ClientDtoMaker.loginInfo, loginInfoDto),
                with(ClientDtoMaker.country, CountryDtoBuilder.default()),
                with(ClientDtoMaker.gender, GenderDtoBuilder.default()),
                with(ClientDtoMaker.coach, CoachDtoBuilder.maker()
                    .but(with(CoachDtoMaker.loginInfo, loginInfoDtoCoach))
                    .make()))
            .make()

        val entity = clientDto.toClient()

        then(entity.id).isEqualTo(clientDto.id)
        then(entity.country).isNotNull
        then(entity.gender).isNotNull
        then(entity.coach).isNotNull
    }

    @Test
    fun `test convert dto to entity illegal args`() {

        val clientDtoLoginInfoNull = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.loginInfo, null as LoginInfoDto?))
            .make()
        val clientDtoClientStatusNull = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, null as ClientStatusDto? ),
                with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()))
            .make()

        val exception0 = catchThrowable { clientDtoLoginInfoNull.toClient() }
        val exception1 = catchThrowable { clientDtoClientStatusNull.toClient() }

        then(exception0).isInstanceOf(IllegalArgumentException::class.java)
        then(exception0).hasMessageContaining("loginInfo can not be null")

        then(exception1).isInstanceOf(IllegalArgumentException::class.java)
        then(exception1).hasMessageContaining("clientStatus can not be null")
    }

}
