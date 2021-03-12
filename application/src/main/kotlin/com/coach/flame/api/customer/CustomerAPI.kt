package com.coach.flame.api.customer

import com.coach.flame.api.customer.request.CustomerRequest
import com.coach.flame.api.customer.response.CustomerResponse

interface CustomerAPI {

    fun registerNewCustomer(customer: CustomerRequest): CustomerResponse
    
    fun getNewCustomerSession(customer: CustomerRequest): CustomerResponse

}