package com.coach.flame.domain.converters

import com.coach.flame.jpa.entity.CountryConfigGenerator
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class CountryDtoConverterTest {

    private val classToTest: CountryDtoConverter = CountryDtoConverter()

    @Test
    fun `client convert all values`() {

        // given
        val country = CountryConfigGenerator.Builder()
            .build()
            .nextObject()

        // when
        val countryDto = classToTest.convert(country)

        //then
        then(countryDto.countryCode).isEqualTo(country.countryCode)
        then(countryDto.externalValue).isEqualTo(country.externalValue)
    }

}