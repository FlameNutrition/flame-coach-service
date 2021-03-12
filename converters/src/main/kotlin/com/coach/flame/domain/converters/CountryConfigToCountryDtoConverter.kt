package com.coach.flame.domain.converters

import com.coach.flame.domain.CountryDto
import com.coach.flame.jpa.entity.CountryConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class CountryConfigToCountryDtoConverter : Converter<CountryConfig, CountryDto> {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CountryConfigToCountryDtoConverter::class.java)
    }

    override fun convert(country: CountryConfig): CountryDto {
        return CountryDto(
            countryCode = country.countryCode,
            externalValue = country.externalValue
        )
    }
}