package com.coach.flame.api.customer

import com.coach.flame.api.customer.request.CustomerRequest
import com.coach.flame.api.customer.request.CustomerRequestConverter
import com.coach.flame.api.customer.response.CustomerResponse
import com.coach.flame.api.customer.response.CustomerResponseConverter
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.client.CustomerService
import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/client")
class CustomerImp(
    private val customerService: CustomerService,
    private val customerRequestConverter: CustomerRequestConverter,
    private val customerResponseConverter: CustomerResponseConverter,
) : CustomerAPI {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CustomerImp::class.java)
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/create")
    @ResponseBody
    override fun registerNewCustomer(@RequestBody(required = true) customer: CustomerRequest): CustomerResponse {
        try {

            requireNotNull(customer.firstname) { "Missing required parameter request: firstname" }
            requireNotNull(customer.lastname) { "Missing required parameter request: lastname" }
            requireNotNull(customer.email) { "Missing required parameter request: email" }
            requireNotNull(customer.password) { "Missing required parameter request: password" }
            requireNotNull(customer.type) { "Missing required parameter request: type" }

            val clientDomain = customerRequestConverter.convert(customer)

            val clientPersisted = customerService.registerCustomer(clientDomain)

            return customerResponseConverter.convert(clientPersisted)

        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='registerNewCustomer', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='registerNewCustomer', msg='Please check following problem'", ex)
                    throw RestException(ex)
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/newSession")
    @ResponseBody
    override fun getNewCustomerSession(@RequestBody(required = true) customer: CustomerRequest): CustomerResponse {
        try {

            requireNotNull(customer.email) { "Missing required parameter request: email" }
            requireNotNull(customer.password) { "Missing required parameter request: password" }

            //FIXME: Send the password encrypted
            val customerPersisted = customerService.getNewCustomerSession(customer.email, customer.password)

            return customerResponseConverter.convert(customerPersisted)

        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='getNewCustomerSession', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='getNewCustomerSession', msg='Please check following problem'", ex)
                    throw RestException(ex)
                }
                else -> {
                    throw ex
                }
            }
        }
    }

}