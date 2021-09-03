package com.coach.flame.api.appointment

import com.coach.flame.appointment.income.IncomeAggregator
import com.coach.flame.appointment.income.IncomeService
import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.domain.IncomeDto
import com.coach.flame.domain.maker.IncomeDtoBuilder
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
class IncomeImplTest {

    @MockK
    private lateinit var incomesService: IncomeService

    @InjectMockKs
    private lateinit var classToTest: IncomeImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get accepted incomes`() {

        val from = LocalDate.of(2021, 4, 1)
        val to = LocalDate.of(2022, 10, 1)

        val uuid = UUID.randomUUID()
        val interval = DateIntervalDto(from, to)

        val mapOfIncomes = mutableMapOf<LocalDate, List<IncomeDto>>()
        mapOfIncomes[LocalDate.of(2021, 1, 1)] = listOf(IncomeDtoBuilder.accepted(), IncomeDtoBuilder.accepted())
        mapOfIncomes[LocalDate.of(2022, 1, 1)] = listOf(IncomeDtoBuilder.accepted())

        every {
            incomesService.getAcceptedIncomes(uuid, IncomeAggregator.Type.YEAR, interval)
        } returns mapOfIncomes

        val result = classToTest.getAcceptedIncomes(uuid, "2021-04-01", "2022-10-01", "YEAR")

        then(result.incomes).hasSize(2)
        then(result.incomes[LocalDate.of(2021, 1, 1)]).hasSize(2)
        then(result.incomes[LocalDate.of(2022, 1, 1)]).hasSize(1)

    }

}
