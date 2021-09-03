package com.coach.flame.metrics.services

import com.coach.flame.domain.IncomeDto
import com.coach.flame.domain.MetricsDto
import com.coach.flame.domain.metrics.Incomes
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.exception.BusinessException
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import com.coach.flame.metrics.MetricsFilter
import com.coach.flame.metrics.MetricsService
import org.springframework.stereotype.Service

@Service("incomesMetricsService")
class IncomeMetricsServiceImpl(
    private val coachOperations: CoachRepositoryOperation,
) : MetricsService {

    override fun getMetrics(metricsFilter: MetricsFilter): MetricsDto {

        val interval = metricsFilter.dateInterval
            ?: throw BusinessException(ErrorCode.CODE_9999, "DateInterval is not defined, for collect income metrics")

        val listOfIncomes = coachOperations.getIncome(metricsFilter.identifier, interval.from, interval.to)

        val numberOfPending = listOfIncomes.count { IncomeDto.IncomeStatus.PENDING.name === it.status }
        val numberOfAccepted = listOfIncomes.count { IncomeDto.IncomeStatus.ACCEPTED.name === it.status }
        val numberOfRejected = listOfIncomes.count { IncomeDto.IncomeStatus.REJECTED.name === it.status }

        return MetricsDto().apply {
            incomes = Incomes(
                total = listOfIncomes.size,
                pending = numberOfPending,
                rejected = numberOfRejected,
                accepted = numberOfAccepted
            )
        }
    }
}
