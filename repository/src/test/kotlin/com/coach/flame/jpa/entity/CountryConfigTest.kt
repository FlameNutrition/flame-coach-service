package com.coach.flame.jpa.entity

import com.coach.flame.domain.maker.CountryDtoBuilder
import com.coach.flame.domain.maker.CountryDtoMaker
import com.coach.flame.jpa.entity.CountryConfig.Companion.toCountryConfig
import com.coach.flame.jpa.entity.maker.*
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class CountryConfigTest {

    @Test
    fun `test convert country config to dto all values`() {

        val country = CountryBuilder.default()

        val dto = country.toDto()

        then(dto.externalValue).isEqualTo(country.externalValue)
        then(dto.countryCode).isEqualTo(country.countryCode)
    }

    @Test
    fun `test convert country dto to entity all values`() {

        val countryDto = CountryDtoBuilder.maker()
            .but(with(CountryDtoMaker.id, 100L))
            .make()

        val entity = countryDto.toCountryConfig()

        then(entity.id).isEqualTo(countryDto.id)
        then(entity.countryCode).isEqualTo(countryDto.countryCode)
        then(entity.externalValue).isEqualTo(countryDto.externalValue)

    }

}
