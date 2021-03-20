package com.coach.flame.api.dailyTask.request

import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class DailyTaskFilterTest {

    @Test
    fun `test verify number of parameters`() {

        // given
        val dailyTaskFilter = DailyTaskFilter(
            type = "IDENTIFIER",
            values = listOf("Test", "Test2"))

        // when
        val thrown =
            catchThrowable { dailyTaskFilter.verifyNumOfParams() }

        //then
        then(thrown)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("IDENTIFIER has an invalid number of params")
    }

}