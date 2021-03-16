package com.coach.flame.domain.converters

import com.coach.flame.domain.GenderDto
import com.coach.flame.jpa.entity.GenderConfig
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class GenderDtoConverter : Converter<GenderConfig, GenderDto> {

    override fun convert(gender: GenderConfig): GenderDto {
        return GenderDto(
            genderCode = gender.genderCode,
            externalValue = gender.externalValue
        )
    }
}