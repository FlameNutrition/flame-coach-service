package com.coach.flame.jpa.entity

import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.jpa.entity.ClientType.Companion.toClientType
import com.coach.flame.jpa.entity.maker.ClientTypeBuilder
import com.coach.flame.jpa.entity.maker.ClientTypeMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class ClientTypeTest {

    @Test
    fun `test convert client type to dto all values`() {

        val clientType = ClientTypeBuilder.maker()
            .but(with(ClientTypeMaker.type, "CLIENT"))
            .make()
        val coachType = ClientTypeBuilder.maker()
            .but(with(ClientTypeMaker.type, "COACH"))
            .make()
        val unknownType = ClientTypeBuilder.maker()
            .but(with(ClientTypeMaker.type, "INVALID"))
            .make()

        val typeClientDto = clientType.toDto()
        val typeCoachDto = coachType.toDto()
        val typeUnknownDto = unknownType.toDto()

        then(typeClientDto.name).isEqualTo(clientType.type)
        then(typeCoachDto.name).isEqualTo(coachType.type)
        then(typeUnknownDto.name).isEqualTo("UNKNOWN")
    }

    @Test
    fun `test convert client type dto to entity all values`() {

        then(CustomerTypeDto.CLIENT.toClientType().id).isEqualTo(2)
        then(CustomerTypeDto.COACH.toClientType().id).isEqualTo(1)
        then(CustomerTypeDto.UNKNOWN.toClientType().id).isEqualTo(0)

    }

}
