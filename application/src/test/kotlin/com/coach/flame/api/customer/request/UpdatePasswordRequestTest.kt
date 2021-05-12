package com.coach.flame.api.customer.request

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class UpdatePasswordRequestTest {

    @Test
    fun `test toString method do not expose password field`() {

        val customerRequest = UpdatePasswordRequest(
            "email@gmail.com",
            "isTheBestPet",
            "isTheBestCat"
        )

        then(customerRequest.toString())
            .doesNotContain("isTheBestPet")
            .doesNotContain("isTheBestCat")
            .contains("******")

    }

}
