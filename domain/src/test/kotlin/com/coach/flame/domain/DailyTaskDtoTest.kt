package com.coach.flame.domain

import com.coach.flame.domain.maker.DailyTaskDtoBuilder
import com.coach.flame.domain.maker.DailyTaskDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class DailyTaskDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = DailyTaskDtoBuilder.maker()
            .but(with(DailyTaskDtoMaker.id, 100L))
            .make()

        then(dto.toString())
            .contains("DailyTaskDto(")
            .doesNotContain("id=")
            .doesNotContain("100")

    }

}
