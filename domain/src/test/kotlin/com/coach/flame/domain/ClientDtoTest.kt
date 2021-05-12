package com.coach.flame.domain

import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class ClientDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.id, 100L))
            .make()

        then(dto.toString())
            .contains("ClientDto(")
            .doesNotContain("id=100")

    }

}
