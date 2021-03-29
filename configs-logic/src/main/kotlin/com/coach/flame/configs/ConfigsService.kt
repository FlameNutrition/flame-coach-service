package com.coach.flame.configs

import com.coach.flame.domain.CountryDto
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.GenderDto

interface ConfigsService {

    fun getCustomerType(key: String): CustomerTypeDto

    fun getCountry(key: String): CountryDto

    fun getGender(key: String): GenderDto

}
