package com.coach.flame.appointment.income

import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.domain.IncomeDto
import com.coach.flame.domain.maker.IncomeDtoBuilder
import com.coach.flame.domain.maker.IncomeDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class IncomeAggregatorTest {

    @Test
    fun `test aggregate incomes by year - full years`() {

        val interval = DateIntervalDto(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2021, 12, 31)
        )

        val aggregatorResult =
            IncomeAggregator(interval, createData()).getIncomes(IncomeAggregator.Type.YEAR, Optional.empty())

        then(aggregatorResult).hasSize(2)
        then(aggregatorResult).containsKeys(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2021, 1, 1)
        )
        then(aggregatorResult.values.first()).hasSize(720) // 2020

    }

    @Test
    fun `test aggregate incomes by year - half years`() {

        val interval = DateIntervalDto(
            LocalDate.of(2020, 6, 1),
            LocalDate.of(2021, 6, 30)
        )

        val aggregatorResult =
            IncomeAggregator(interval, createData()).getIncomes(IncomeAggregator.Type.YEAR, Optional.empty())

        then(aggregatorResult).hasSize(2)
        then(aggregatorResult).containsKeys(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2021, 1, 1)
        )
        then(aggregatorResult.values.first()).hasSize(420) //2020
        then(aggregatorResult.values.last()).hasSize(360) //2021
    }

    @Test
    fun `test aggregate incomes by month - 12 months`() {

        val interval = DateIntervalDto(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 12, 31)
        )

        val aggregatorResult =
            IncomeAggregator(interval, createData()).getIncomes(IncomeAggregator.Type.MONTH, Optional.empty())

        then(aggregatorResult).hasSize(12)
        then(aggregatorResult).containsKeys(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 12, 1)
        )
        then(aggregatorResult.values.first()).hasSize(60)
    }

    @Test
    fun `test aggregate incomes by month - 6 months 2020 and 6 months 2021`() {

        val interval = DateIntervalDto(
            LocalDate.of(2020, 6, 1),
            LocalDate.of(2021, 6, 30)
        )

        val aggregatorResult =
            IncomeAggregator(interval, createData()).getIncomes(IncomeAggregator.Type.MONTH, Optional.empty())

        then(aggregatorResult).hasSize(13)
        then(aggregatorResult).containsKeys(
            LocalDate.of(2020, 6, 1),
            LocalDate.of(2021, 1, 1),
            LocalDate.of(2021, 6, 1)
        )
        then(aggregatorResult.values.first()).hasSize(60)
    }

    @Test
    fun `test aggregate incomes by day - 10 days 2020 jan`() {

        val interval = DateIntervalDto(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 10)
        )

        val aggregatorResult =
            IncomeAggregator(interval, createData()).getIncomes(IncomeAggregator.Type.DAY, Optional.empty())

        then(aggregatorResult).hasSize(10)
        then(aggregatorResult).containsKeys(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 10)
        )
        then(aggregatorResult.values.first()).hasSize(4)
    }

    @Test
    fun `test aggregate incomes by day and filter only accepted incomes - 10 days 2020 jan`() {

        val interval = DateIntervalDto(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 10)
        )

        val aggregatorResult = IncomeAggregator(interval, createData()).getIncomes(IncomeAggregator.Type.DAY,
            Optional.of(IncomeAggregator.FILTER_ACCEPTED))

        then(aggregatorResult).hasSize(10)
        then(aggregatorResult).containsKeys(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 10)
        )
        then(aggregatorResult.values.first()).hasSize(2)
    }

    @Test
    fun `test aggregate incomes by day and filter only rejected incomes - 10 days 2020 jan`() {

        val interval = DateIntervalDto(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 10)
        )

        val aggregatorResult = IncomeAggregator(interval, createData()).getIncomes(IncomeAggregator.Type.DAY,
            Optional.of(IncomeAggregator.FILTER_REJECTED))

        then(aggregatorResult).hasSize(10)
        then(aggregatorResult).containsKeys(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 10)
        )
        then(aggregatorResult.values.first()).hasSize(1)
    }

    @Test
    fun `test aggregate incomes by day and filter only pending incomes - 10 days 2020 jan`() {

        val interval = DateIntervalDto(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 10)
        )

        val aggregatorResult = IncomeAggregator(interval, createData()).getIncomes(IncomeAggregator.Type.DAY,
            Optional.of(IncomeAggregator.FILTER_PENDING))

        then(aggregatorResult).hasSize(10)
        then(aggregatorResult).containsKeys(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 10)
        )
        then(aggregatorResult.values.first()).hasSize(1)
    }

    private fun createData(): Map<LocalDate, List<IncomeDto>> {

        val data = mutableMapOf<LocalDate, List<IncomeDto>>()

        IntRange(1, 12).forEach { month ->
            //Create 15 days in each month
            IntRange(1, 15).forEach { day ->
                //Each day with 2 accepted, 1 reject, 1 pending Appointments -> Incomes
                data[LocalDate.of(2020, month, day)] =
                    listOf(
                        createAcceptedIncome(100.56f),
                        createAcceptedIncome(120.52f),
                        createRejectedIncome(76.54f),
                        createPendingIncome(54.51f)
                    )
            }

            IntRange(1, 15).forEach { day ->
                //Each day with 2 accepted, 1 reject, 1 pending Appointments -> Incomes
                data[LocalDate.of(2021, month, day)] =
                    listOf(
                        createAcceptedIncome(100.56f),
                        createAcceptedIncome(120.52f),
                        createRejectedIncome(76.54f),
                        createPendingIncome(54.51f)
                    )
            }
        }

        return data
    }

    private fun createAcceptedIncome(price: Float): IncomeDto {
        return IncomeDtoBuilder.maker()
            .but(with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.ACCEPTED),
                with(IncomeDtoMaker.price, price))
            .make()
    }

    private fun createRejectedIncome(price: Float): IncomeDto {
        return IncomeDtoBuilder.maker()
            .but(with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.REJECTED),
                with(IncomeDtoMaker.price, price))
            .make()
    }

    private fun createPendingIncome(price: Float): IncomeDto {
        return IncomeDtoBuilder.maker()
            .but(with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.PENDING),
                with(IncomeDtoMaker.price, price))
            .make()
    }

}
