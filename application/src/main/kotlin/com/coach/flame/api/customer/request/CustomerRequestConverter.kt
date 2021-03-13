package com.coach.flame.api.customer.request

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.LoginInfoDto
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.util.*

@Component
class CustomerRequestConverter : Converter<CustomerRequest, ClientDto> {

    override fun convert(customerRequest: CustomerRequest): ClientDto {


        val clientTypeDto = customerRequest.type?.let {
            try {
                CustomerTypeDto.valueOf(customerRequest.type.toUpperCase())
            } catch (ex: Exception) {
                throw IllegalArgumentException("Invalid value in type parameter")
            }
        }

        return ClientDto(
            identifier = UUID.randomUUID(),
            firstName = customerRequest.firstname!!,
            lastName = customerRequest.lastname!!,
            customerType = clientTypeDto!!,
            loginInfo = LoginInfoDto(
                username = customerRequest.email!!,
                password = customerRequest.password!!,
            ),
            clientStatus = null
        )
    }

}