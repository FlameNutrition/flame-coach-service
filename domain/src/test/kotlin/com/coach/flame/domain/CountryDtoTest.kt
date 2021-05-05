package com.coach.flame.domain

import com.coach.flame.domain.maker.CountryDtoBuilder
import com.coach.flame.domain.maker.CountryDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class CountryDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = CountryDtoBuilder.maker()
            .but(with(CountryDtoMaker.id, 100L))
            .make()

        then(dto.toString())
            .contains("CountryDto(")
            .doesNotContain("id=")
            .doesNotContain("100")

    }

}
