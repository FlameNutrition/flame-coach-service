package com.coach.flame.api.customer.request

import com.coach.flame.domain.*
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

@Component
class CustomerRequestConverter : Converter<CustomerRequest, Customer> {

    override fun convert(customerRequest: CustomerRequest): Customer {

        val clientTypeDto = customerRequest.type?.let {
            try {
                CustomerTypeDto.valueOf(customerRequest.type.toUpperCase())
            } catch (ex: Exception) {
                throw IllegalArgumentException("Invalid value in type parameter")
            }
        }

        return if (CustomerTypeDto.COACH == clientTypeDto) {
            CoachDto(
                identifier = UUID.randomUUID(),
                firstName = customerRequest.firstname!!,
                lastName = customerRequest.lastname!!,
                customerType = clientTypeDto,
                loginInfo = LoginInfoDto(
                    userId = null,
                    sessionId = null,
                    username = customerRequest.email!!,
                    password = customerRequest.password!!,
                    keyDecrypt = null
                ),
                registrationDate = LocalDate.now()
            )
        } else {
            requireNotNull(customerRequest.registrationKey) { "missing required parameter: registrationKey" }
            ClientDto(
                identifier = UUID.randomUUID(),
                firstName = customerRequest.firstname!!,
                lastName = customerRequest.lastname!!,
                customerType = clientTypeDto!!,
                loginInfo = LoginInfoDto(
                    userId = null,
                    sessionId = null,
                    username = customerRequest.email!!,
                    password = customerRequest.password!!,
                    keyDecrypt = null
                ),
                clientStatus = null,
                registrationDate = LocalDate.now(),
                coach = null,
                weight = 0.0f,
                height = 0.0f,
                measureType = MeasureTypeDto.KG_CM,
                registrationKey = customerRequest.registrationKey
            )
        }

    }

}
