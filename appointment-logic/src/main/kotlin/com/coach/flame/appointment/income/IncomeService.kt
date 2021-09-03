package com.coach.flame.appointment.income

import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.domain.IncomeDto
import java.time.LocalDate
import java.util.*

interface IncomeService {

    fun getAcceptedIncomes(
        coachIdentifier: UUID,
        aggregatorType: IncomeAggregator.Type,
        interval: DateIntervalDto,
    ): Map<LocalDate, List<IncomeDto>>

}
