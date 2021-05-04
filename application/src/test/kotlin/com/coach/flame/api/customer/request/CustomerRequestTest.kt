package com.coach.flame.api.customer.request

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class CustomerRequestTest {

    @Test
    fun `test toString method do not expose password field`() {

        val customerRequest = CustomerRequest(
            "My name",
            "My last name",
            "My email",
            "isTheBestPet",
            "COACH",
            true
        )

        then(customerRequest.toString())
            .doesNotContain("isTheBestPet")
            .contains("******")

    }

}
