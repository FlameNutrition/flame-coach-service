package com.coach.flame.api.customer

import com.coach.flame.api.customer.request.CustomerRequest
import com.coach.flame.api.customer.request.CustomerRequestConverter
import com.coach.flame.api.customer.request.UpdatePasswordRequest
import com.coach.flame.api.customer.response.CustomerResponse
import com.coach.flame.api.customer.response.CustomerResponseConverter
import com.coach.flame.api.customer.response.UpdatePasswordResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.customer.CustomerService
import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import com.coach.flame.failure.domain.ErrorCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/customer")
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

            requireNotNull(customer.firstname) { "missing required parameter: firstname" }
            requireNotNull(customer.lastname) { "missing required parameter: lastname" }
            requireNotNull(customer.email) { "missing required parameter: email" }
            requireNotNull(customer.password) { "missing required parameter: password" }
            requireNotNull(customer.type) { "missing required parameter: type" }

            val customerDomain = customerRequestConverter.convert(customer)

            val clientPersisted = customerService.registerCustomer(customerDomain)

            return customerResponseConverter.convert(clientPersisted)

        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='registerNewCustomer', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex.localizedMessage, ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='registerNewCustomer', msg='Please check following problem'", ex)
                    throw RestException(ErrorCode.CODE_1000, ex.localizedMessage, ex)
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

            val customerPersisted = customerService.getNewCustomerSession(customer.email, customer.password)

            return customerResponseConverter.convert(customerPersisted)

        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='getNewCustomerSession', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex.localizedMessage, ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='getNewCustomerSession', msg='Please check following problem'", ex)
                    throw RestException(ErrorCode.CODE_1000, ex.localizedMessage, ex)
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/updatePassword")
    @ResponseBody
    override fun updatePassword(@RequestBody(required = true) request: UpdatePasswordRequest): UpdatePasswordResponse {

        customerService.updateCustomerPassword(request.email, request.oldPassword, request.newPassword)

        return UpdatePasswordResponse(true)

    }

}
