package com.coach.flame.domain

import com.coach.flame.domain.maker.AppointmentDtoBuilder
import com.coach.flame.domain.maker.AppointmentDtoMaker
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class AppointmentDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.id, 100L))
            .make()

        then(dto.toString())
            .contains("AppointmentDto(")
            .doesNotContain("id=100")

    }

}
