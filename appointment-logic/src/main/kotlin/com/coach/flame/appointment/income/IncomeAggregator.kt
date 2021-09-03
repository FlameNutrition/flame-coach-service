package com.coach.flame.appointment.income

import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.domain.IncomeDto
import java.time.LocalDate
import java.util.*
import java.util.function.Predicate
import kotlin.streams.toList

class IncomeAggregator(
    private val intervalDto: DateIntervalDto,
    private val incomes: Map<LocalDate, List<IncomeDto>>,
) {

    companion object {
        val FILTER_ACCEPTED = Predicate<IncomeDto> { it.status == IncomeDto.IncomeStatus.ACCEPTED }
        val FILTER_REJECTED = Predicate<IncomeDto> { it.status == IncomeDto.IncomeStatus.REJECTED }
        val FILTER_PENDING = Predicate<IncomeDto> { it.status == IncomeDto.IncomeStatus.PENDING }
    }

    enum class Type {
        YEAR, MONTH, DAY
    }

    /**
     * Get incomes aggregated by [aggregationType] attribute. System will order the incomes using
     * natural sort.
     * e.g:
     *  year -> 2010, 2011, 2012
     *  month -> 01, 02, 03, 04
     *  day -> 01, 02, 03
     *
     * @param aggregationType aggregation type
     * @return all incomes aggregated by the type you choose
     */
    fun getIncomes(aggregationType: Type, filter: Optional<Predicate<IncomeDto>>): Map<LocalDate, List<IncomeDto>> {
        return when (aggregationType) {
            Type.YEAR -> aggregateByYear(filter)
            Type.MONTH -> aggregateByMonth(filter)
            Type.DAY -> aggregateByDay(filter)
        }
    }

    private fun filterIncomesStatus(
        incomes: List<IncomeDto>,
        filter: Optional<Predicate<IncomeDto>>,
    ): MutableList<IncomeDto> {

        if (filter.isEmpty) {
            return incomes.toMutableList()
        }

        return incomes.stream()
            .filter(filter.get())
            .toList()
            .toMutableList()
    }

    private fun aggregateByYear(filter: Optional<Predicate<IncomeDto>>): Map<LocalDate, List<IncomeDto>> {
        val aggregationMap = mutableMapOf<LocalDate, MutableList<IncomeDto>>()

        incomes.forEach { (date, listOfIncomes) ->
            if (intervalDto.isBetweenInterval(date)) {
                val startOfYear = date
                    .withMonth(1)
                    .withDayOfMonth(1)

                val incomes = filterIncomesStatus(listOfIncomes, filter)

                if (aggregationMap.containsKey(startOfYear)) {
                    aggregationMap[startOfYear]?.addAll(incomes)
                } else {
                    aggregationMap[startOfYear] = incomes
                }
            }
        }

        return aggregationMap.toSortedMap()
    }

    private fun aggregateByMonth(filter: Optional<Predicate<IncomeDto>>): Map<LocalDate, List<IncomeDto>> {
        val aggregationMap = mutableMapOf<LocalDate, MutableList<IncomeDto>>()

        incomes.forEach { (date, listOfIncomes) ->
            if (intervalDto.isBetweenInterval(date)) {
                val startOfMonth = date.withDayOfMonth(1)

                val incomes = filterIncomesStatus(listOfIncomes, filter)

                if (aggregationMap.containsKey(startOfMonth)) {
                    aggregationMap[startOfMonth]?.addAll(incomes)
                } else {
                    aggregationMap[startOfMonth] = incomes
                }
            }
        }

        return aggregationMap.toSortedMap()
    }

    private fun aggregateByDay(filter: Optional<Predicate<IncomeDto>>): Map<LocalDate, List<IncomeDto>> {
        val aggregationMap = mutableMapOf<LocalDate, MutableList<IncomeDto>>()

        incomes.forEach { (date, listOfIncomes) ->
            if (intervalDto.isBetweenInterval(date)) {
                val incomes = filterIncomesStatus(listOfIncomes, filter)

                if (aggregationMap.containsKey(date)) {
                    aggregationMap[date]?.addAll(incomes)
                } else {
                    aggregationMap[date] = incomes
                }
            }
        }

        return aggregationMap.toSortedMap()
    }

}
