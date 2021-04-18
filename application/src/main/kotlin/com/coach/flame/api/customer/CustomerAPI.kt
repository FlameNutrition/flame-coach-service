package com.coach.flame.api.customer

import com.coach.flame.api.customer.request.CustomerRequest
import com.coach.flame.api.customer.request.UpdatePasswordRequest
import com.coach.flame.api.customer.response.CustomerResponse
import com.coach.flame.api.customer.response.UpdatePasswordResponse

interface CustomerAPI {

    fun registerNewCustomer(customer: CustomerRequest): CustomerResponse

    fun getNewCustomerSession(customer: CustomerRequest): CustomerResponse

    fun updatePassword(request: UpdatePasswordRequest): UpdatePasswordResponse

}
