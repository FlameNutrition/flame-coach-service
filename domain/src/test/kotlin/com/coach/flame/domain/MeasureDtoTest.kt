package com.coach.flame.domain

import com.coach.flame.domain.maker.MeasureDtoBuilder
import com.coach.flame.domain.maker.MeasureDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class MeasureDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = MeasureDtoBuilder.maker()
            .but(with(MeasureDtoMaker.id, 100L))
            .make()

        then(dto.toString())
            .contains("MeasureDto(")
            .doesNotContain("id=100")

    }

}
