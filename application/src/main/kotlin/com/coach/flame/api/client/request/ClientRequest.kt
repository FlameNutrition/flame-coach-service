package com.coach.flame.api.client.request

data class ClientRequest(
    val firstname: String?,
    val lastname: String?,
    val email: String?,
    val password: String?,
    val type: String?,
    val policy: Boolean?
)
