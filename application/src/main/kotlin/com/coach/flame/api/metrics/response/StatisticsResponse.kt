package com.coach.flame.api.metrics.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StatisticsResponse(val identifier: UUID) {

    var clientsStatus: ClientsStatusResponse? = null

    var incomesStatus: IncomesStatusResponse? = null

}


