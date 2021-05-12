package com.coach.flame.jpa.entity

import com.coach.flame.domain.maker.GenderDtoBuilder
import com.coach.flame.domain.maker.GenderDtoMaker
import com.coach.flame.jpa.entity.GenderConfig.Companion.toGenderConfig
import com.coach.flame.jpa.entity.maker.GenderBuilder
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class GenderConfigTest {

    @Test
    fun `test convert gender config to dto all values`() {

        val gender = GenderBuilder.default()

        val dto = gender.toDto()

        then(dto.externalValue).isEqualTo(gender.externalValue)
        then(dto.genderCode).isEqualTo(gender.genderCode)
    }

    @Test
    fun `test convert gender dto to entity all values`() {

        val genderDto = GenderDtoBuilder.maker()
            .but(with(GenderDtoMaker.id, 100L))
            .make()

        val entity = genderDto.toGenderConfig()

        then(entity.id).isEqualTo(genderDto.id)
        then(entity.genderCode).isEqualTo(genderDto.genderCode)
        then(entity.externalValue).isEqualTo(genderDto.externalValue)

    }

}
