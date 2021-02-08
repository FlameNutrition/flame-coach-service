package com.coach.flame.base

import com.coach.flame.FlameCoachServiceApplication
import com.coach.flame.jpa.repository.DailyTaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [FlameCoachServiceApplication::class])
@AutoConfigureTestDatabase
@Transactional
class TestingSuite(
    @Autowired private val dailyTaskRepository: DailyTaskRepository
) {

    @Test
    @Transactional
    @Sql("/dailyRepository/listOfDailyTasks.sql")
    fun `test daily repository`() {

    }

}