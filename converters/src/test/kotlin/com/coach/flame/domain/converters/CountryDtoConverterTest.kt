package com.coach.flame.domain.converters

import com.coach.flame.jpa.entity.CountryMaker
import com.natpryce.makeiteasy.MakeItEasy.an
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class CountryDtoConverterTest {

    private val classToTest: CountryDtoConverter = CountryDtoConverter()

    private val countryMaker = an(CountryMaker.CountryConfig)

    @Test
    fun `client convert all values`() {

        // given
        val country = countryMaker.make()

        // when
        val countryDto = classToTest.convert(country)

        //then
        then(countryDto.countryCode).isEqualTo(country.countryCode)
        then(countryDto.externalValue).isEqualTo(country.externalValue)
    }

}