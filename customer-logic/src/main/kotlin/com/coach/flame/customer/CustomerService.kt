package com.coach.flame.customer

import com.coach.flame.domain.Customer
import com.coach.flame.domain.CustomerTypeDto
import java.util.*

//TODO: Write documentation
interface CustomerService {

    fun getCustomer(uuid: UUID, customerType: CustomerTypeDto): Customer

    fun updateCustomer(uuid: UUID, customer: Customer): Customer

    fun registerCustomer(customer: Customer): Customer

    fun getNewCustomerSession(username: String, password: String): Customer

    fun updateCustomerPassword(email: String, oldPassword: String, newPassword: String)

}
