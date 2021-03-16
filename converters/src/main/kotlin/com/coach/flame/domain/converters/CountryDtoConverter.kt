package com.coach.flame.domain.converters

import com.coach.flame.domain.CountryDto
import com.coach.flame.jpa.entity.CountryConfig
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class CountryDtoConverter : Converter<CountryConfig, CountryDto> {

    override fun convert(country: CountryConfig): CountryDto {
        return CountryDto(
            countryCode = country.countryCode,
            externalValue = country.externalValue
        )
    }
}