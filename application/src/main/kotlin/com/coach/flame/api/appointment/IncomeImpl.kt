package com.coach.flame.api.appointment

import com.coach.flame.api.APIWrapperException
import com.coach.flame.api.appointment.response.Income
import com.coach.flame.api.appointment.response.IncomeResponse
import com.coach.flame.appointment.income.IncomeAggregator
import com.coach.flame.appointment.income.IncomeService
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.date.DateHelper
import com.coach.flame.domain.DateIntervalDto
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/income")
class IncomeImpl(
    private val incomesService: IncomeService,
) : IncomeAPI {

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/coach/getAcceptedIncomes")
    @ResponseBody
    override fun getAcceptedIncomes(
        @RequestParam(required = true) coachIdentifier: UUID,
        @RequestParam(required = true) from: String,
        @RequestParam(required = true) to: String,
        @RequestParam(required = false, defaultValue = "MONTH") aggregateType: String,
    ): IncomeResponse {
        return APIWrapperException.executeRequest {

            val interval = DateIntervalDto(DateHelper.toDate(from), DateHelper.toDate(to))
            val aggregatorType = IncomeAggregator.Type.valueOf(aggregateType)

            val incomes = incomesService.getAcceptedIncomes(coachIdentifier, aggregatorType, interval)
                .mapValues { it.value.map { income -> income.price } }
                .toMap()

            IncomeResponse(incomes = incomes)
        }
    }
}
