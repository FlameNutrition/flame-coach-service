package com.coach.flame.api.metrics

import com.coach.flame.api.APIWrapperException
import com.coach.flame.api.metrics.response.ClientsStatisticsResponse
import com.coach.flame.api.metrics.response.ClientsStatusResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.metrics.MetricsService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/metrics")
class MetricsImpl(
    private val metricsService: MetricsService,
) : MetricsAPI {

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/clients")
    @ResponseBody
    override fun getClientsStatistics(@RequestParam(required = true) coachIdentifier: UUID): ClientsStatisticsResponse {
        return APIWrapperException.executeRequest {
            val metricsDto = metricsService.getMetrics(coachIdentifier)

            ClientsStatisticsResponse(coachIdentifier).apply {
                clientsStatus = ClientsStatusResponse(
                    numberOfClientsPending = metricsDto.clients.numberOfClientsPending,
                    numberOfClientsAccepted = metricsDto.clients.numberOfClientsAccepted,
                    numberOfTotalClients = metricsDto.clients.numberOfTotalClients)
            }
        }
    }
}

