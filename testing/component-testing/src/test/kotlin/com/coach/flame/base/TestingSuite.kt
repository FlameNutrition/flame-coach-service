package com.coach.flame.base

import com.coach.flame.FlameCoachServiceApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [FlameCoachServiceApplication::class])
@AutoConfigureTestDatabase
@Transactional
class TestingSuite {

    @Test
    fun `A sample test`() {
        assertThat("This is a sample test").isEqualTo("This is a sample test")
    }
}