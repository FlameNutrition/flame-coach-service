package com.coach.flame.metrics.services

import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.jpa.entity.Income
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import com.coach.flame.metrics.MetricsFilter
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class IncomeMetricsServiceImplTest {

    @MockK(relaxed = true)
    private lateinit var coachRepositoryOperation: CoachRepositoryOperation

    @InjectMockKs
    private lateinit var classToTest: IncomeMetricsServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get clients metrics`() {

        val uuid = UUID.randomUUID()
        val from = LocalDate.of(2021, 5, 20)
        val to = LocalDate.of(2021, 6, 20)

        val pendingIncome = Income()
            .apply {
                status = "PENDING"
            }
        val acceptedIncome = Income()
            .apply {
                status = "ACCEPTED"
            }
        val rejectedIncome = Income()
            .apply {
                status = "REJECTED"
            }

        every { coachRepositoryOperation.getIncome(uuid, from, to) } returns listOf(
            pendingIncome, pendingIncome, acceptedIncome, rejectedIncome
        )

        val filter = MetricsFilter(uuid)
            .apply {
                dateInterval = DateIntervalDto(from, to)
            }

        val result = classToTest.getMetrics(filter)

        then(result.incomes?.accepted).isEqualTo(1)
        then(result.incomes?.pending).isEqualTo(2)
        then(result.incomes?.rejected).isEqualTo(1)
        then(result.incomes?.total).isEqualTo(4)

    }

}
