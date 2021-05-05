package com.coach.flame.domain

import com.coach.flame.domain.maker.CoachDtoBuilder
import com.coach.flame.domain.maker.CoachDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class CoachDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.id, 100L))
            .make()

        then(dto.toString())
            .contains("CoachDto(")
            .doesNotContain("id=")
            .doesNotContain("100")

    }

}
