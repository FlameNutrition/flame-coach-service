package com.coach.flame.domain

import com.coach.flame.domain.maker.GenderDtoBuilder
import com.coach.flame.domain.maker.GenderDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class GenderDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = GenderDtoBuilder.maker()
            .but(with(GenderDtoMaker.id, 100L))
            .make()

        then(dto.toString())
            .contains("GenderDto(")
            .doesNotContain("id=100")

    }

}
