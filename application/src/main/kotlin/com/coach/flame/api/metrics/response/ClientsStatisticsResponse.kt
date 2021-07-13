package com.coach.flame.api.metrics.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ClientsStatisticsResponse(
    val coachIdentifier: UUID,
    var clientsStatus: ClientsStatusResponse = ClientsStatusResponse(),
)


