package com.coach.flame.api.metrics

import com.coach.flame.api.APIWrapperException
import com.coach.flame.api.metrics.response.ClientsStatusResponse
import com.coach.flame.api.metrics.response.IncomesStatusResponse
import com.coach.flame.api.metrics.response.StatisticsResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.date.DateHelper
import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.metrics.MetricsFilter
import com.coach.flame.metrics.MetricsService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/metrics")
class MetricsImpl(
    private val clientsMetricsService: MetricsService,
    private val incomesMetricsService: MetricsService,
) : MetricsAPI {

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/clients")
    @ResponseBody
    override fun getClientsStatistics(@RequestParam(required = true) coachIdentifier: UUID): StatisticsResponse {
        return APIWrapperException.executeRequest {

            val filter = MetricsFilter(coachIdentifier)

            val metricsDto = clientsMetricsService.getMetrics(filter)

            StatisticsResponse(coachIdentifier).apply {
                clientsStatus = metricsDto.clients?.let {
                    ClientsStatusResponse(
                        numberOfClientsPending = it.numberOfClientsPending,
                        numberOfClientsAccepted = it.numberOfClientsAccepted,
                        numberOfTotalClients = it.numberOfTotalClients
                    )
                }
            }
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/incomes")
    @ResponseBody
    override fun getIncomesStatistics(
        @RequestParam(required = true) coachIdentifier: UUID,
        @RequestParam(required = true) from: String,
        @RequestParam(required = true) to: String,
    ): StatisticsResponse {
        return APIWrapperException.executeRequest {

            val dateFrom = DateHelper.toDate(from)
            val dateTo = DateHelper.toDate(to)

            val filter = MetricsFilter(coachIdentifier)
                .apply {
                    dateInterval = DateIntervalDto(dateFrom, dateTo)
                }

            val metricsDto = incomesMetricsService.getMetrics(filter)

            StatisticsResponse(coachIdentifier)
                .apply {
                    incomesStatus = metricsDto.incomes?.let {
                        IncomesStatusResponse(
                            accepted = it.accepted,
                            rejected = it.rejected,
                            pending = it.pending
                        )
                    }
                }
        }
    }
}

