package com.coach.flame.customer

import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.Customer
import java.util.*

//TODO: Write documentation
interface CustomerService {

    fun getCustomer(uuid: UUID, customerType: CustomerTypeDto): Customer

    fun registerCustomer(customer: Customer): Customer

    fun getNewCustomerSession(username: String, password: String): Customer

}