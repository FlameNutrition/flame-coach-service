package com.coach.flame.configs

import com.coach.flame.domain.CountryDto
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.GenderDto
import com.coach.flame.jpa.entity.ClientType
import com.coach.flame.jpa.entity.CountryConfig
import com.coach.flame.jpa.entity.GenderConfig
import com.coach.flame.jpa.repository.cache.ConfigCache
import org.springframework.stereotype.Service

@Service
class ConfigsServiceImpl(
    private val customerTypeCache: ConfigCache<ClientType>,
    private val countryConfigCache: ConfigCache<CountryConfig>,
    private val genderConfigCache: ConfigCache<GenderConfig>,
) : ConfigsService {

    override fun getCustomerType(key: String): CustomerTypeDto {
        val value = customerTypeCache.getValue(key)

        return if (value.isPresent) {
            CustomerTypeDto.valueOf(value.get().type.toUpperCase())
        } else {
            throw UnexpectedConfigException("Customer Type: '$key' is not present in the system.")
        }
    }

    override fun getCountry(key: String): CountryDto {
        val value = countryConfigCache.getValue(key)

        return if (value.isPresent) {
            CountryDto(value.get().id, value.get().countryCode, value.get().externalValue)
        } else {
            throw UnexpectedConfigException("Country Code: '$key' is not present in the system.")
        }
    }

    override fun getGender(key: String): GenderDto {
        val value = genderConfigCache.getValue(key)

        return if (value.isPresent) {
            GenderDto(value.get().id, value.get().genderCode, value.get().externalValue)
        } else {
            throw UnexpectedConfigException("Gender Code: '$key' is not present in the system.")
        }
    }

}
