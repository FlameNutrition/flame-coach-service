package com.coach.flame.domain

import com.coach.flame.domain.maker.MeasureWeightDtoBuilder
import com.coach.flame.domain.maker.MeasureWeightDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class MeasureWeightDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = MeasureWeightDtoBuilder.maker()
            .but(with(MeasureWeightDtoMaker.id, 100L))
            .make()

        then(dto.toString())
            .contains("MeasureWeightDto(")
            .doesNotContain("id=")
            .doesNotContain("100")

    }

}
