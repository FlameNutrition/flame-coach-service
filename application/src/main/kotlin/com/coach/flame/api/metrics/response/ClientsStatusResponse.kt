package com.coach.flame.api.metrics.response

data class ClientsStatusResponse(
    val numberOfClientsAccepted: Int = 0,
    val numberOfClientsPending: Int = 0,
    val numberOfTotalClients: Int = 0,
)
