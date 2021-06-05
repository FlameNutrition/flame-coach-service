package com.coach.flame.api.customer.request

import com.natpryce.makeiteasy.MakeItEasy.a
import com.natpryce.makeiteasy.MakeItEasy.make
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class CustomerRequestTest {

    @Test
    fun `test toString method do not expose password field`() {

        val customerRequest = make(a(CustomerRequestMaker.CustomerRequest))

        then(customerRequest.toString())
            .doesNotContain("isTheBestPet")
            .contains("******")

    }

}
