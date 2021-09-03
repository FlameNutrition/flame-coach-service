package com.coach.flame.domain.metrics

data class Clients(
    val numberOfClientsPending: Int = 0,
    val numberOfClientsAccepted: Int = 0,
    val numberOfTotalClients: Int = 0,
) {
    override fun toString(): String {
        return "Client(" +
                "numberOfClientsPending=$numberOfClientsPending, " +
                "numberOfClientsAccepted=$numberOfClientsAccepted, " +
                "numberOfTotalClients=$numberOfTotalClients" +
                ")"
    }
}
