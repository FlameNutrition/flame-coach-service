package com.coach.flame.domain.converters

import com.coach.flame.jpa.entity.GenderConfigGenerator
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class GenderDtoConverterTest {

    private val classToTest: GenderDtoConverter = GenderDtoConverter()

    @Test
    fun `client convert all values`() {

        // given
        val gender = GenderConfigGenerator.Builder()
            .build()
            .nextObject()

        // when
        val genderDto = classToTest.convert(gender)

        //then
        then(genderDto.genderCode).isEqualTo(gender.genderCode)
        then(genderDto.externalValue).isEqualTo(gender.externalValue)
    }

}