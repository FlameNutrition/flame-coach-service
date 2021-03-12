package com.coach.flame.api.customer.response

import com.coach.flame.domain.Customer
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class CustomerResponseConverter : Converter<Customer, CustomerResponse> {

    override fun convert(customer: Customer): CustomerResponse {

        requireNotNull(customer.firstName) { "firstName should not be null" }
        requireNotNull(customer.lastName) { "lastName should not be null" }
        requireNotNull(customer.loginInfo) { "loginInfo should not be null" }
        requireNotNull(customer.loginInfo?.token) { "loginInfo->token should not be null" }
        requireNotNull(customer.loginInfo?.username) { "loginInfo->username should not be null" }
        requireNotNull(customer.loginInfo?.expirationDate) { "loginInfo->expirationDate should not be null" }
        requireNotNull(customer.customerType) { "clientType should not be null" }

        return CustomerResponse(
            username = customer.loginInfo?.username!!,
            firstname = customer.firstName,
            lastname = customer.lastName,
            token = customer.loginInfo?.token!!,
            expiration = customer.loginInfo?.expirationDate!!,
            type = customer.customerType.name,
            identifier = customer.identifier
        )

    }

}