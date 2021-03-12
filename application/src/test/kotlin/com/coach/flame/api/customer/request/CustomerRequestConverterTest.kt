package com.coach.flame.api.customer.request

import com.coach.flame.domain.CustomerTypeDto
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.with
import com.natpryce.makeiteasy.Maker
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CustomerRequestConverterTest {

    private val classToTest = CustomerRequestConverter()

    private lateinit var customerRequestMaker: Maker<CustomerRequest>

    @BeforeEach
    fun setUp() {
        customerRequestMaker = MakeItEasy.an(CustomerRequestMaker.CustomerRequest)
    }

    @Test
    fun `convert request with invalid parameters`() {

        // given
        val userRequest = customerRequestMaker
            .but(with(CustomerRequestMaker.type, "INVALID"))
            .make()

        // when
        val thrown = catchThrowable { classToTest.convert(userRequest) }

        //then
        then(thrown)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Invalid value in type parameter")

    }

    @Test
    fun `convert request successfully`() {

        // given
        val userRequest = customerRequestMaker
            .make()

        // when
        val client = classToTest.convert(userRequest)

        //then
        then(client.loginInfo).isNotNull
        then(client.customerType).isEqualTo(CustomerTypeDto.CLIENT)
        then(client.firstName).isEqualTo(userRequest.firstname)
        then(client.lastName).isEqualTo(userRequest.lastname)
        then(client.loginInfo!!.username).isEqualTo(userRequest.email)
        then(client.loginInfo!!.password).isEqualTo(userRequest.password)

    }

}