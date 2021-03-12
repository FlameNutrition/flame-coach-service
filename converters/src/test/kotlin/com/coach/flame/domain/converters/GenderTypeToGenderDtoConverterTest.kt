package com.coach.flame.domain.converters

import com.coach.flame.jpa.entity.GenderMaker
import com.natpryce.makeiteasy.MakeItEasy.an
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class GenderTypeToGenderDtoConverterTest {

    private val classToTest: GenderConfigToGenderDtoConverter = GenderConfigToGenderDtoConverter()

    private val genderMaker = an(GenderMaker.GenderConfig)

    @Test
    fun `client convert all values`() {

        // given
        val gender = genderMaker.make()

        // when
        val genderDto = classToTest.convert(gender)

        //then
        then(genderDto.genderCode).isEqualTo(gender.genderCode)
        then(genderDto.externalValue).isEqualTo(gender.externalValue)
    }

}