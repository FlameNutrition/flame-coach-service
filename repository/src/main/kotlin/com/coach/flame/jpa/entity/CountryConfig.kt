package com.coach.flame.jpa.entity

import com.coach.flame.domain.CountryDto
import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "Country_Config")
class CountryConfig(

    @Column(nullable = false)
    val countryCode: String,

    @Column(nullable = false)
    val externalValue: String,

    ) : AbstractPersistable<Long>(), ConfigKey {

    override fun configKey(): String {
        return countryCode
    }

    fun toDto(): CountryDto {
        return CountryDto(
            id = this.id,
            countryCode = this.countryCode,
            externalValue = this.externalValue
        )
    }

    companion object {
        fun CountryDto.toCountryConfig(): CountryConfig {
            val countryConfig = CountryConfig(countryCode, externalValue)
            countryConfig.id = id
            return countryConfig
        }
    }

}
