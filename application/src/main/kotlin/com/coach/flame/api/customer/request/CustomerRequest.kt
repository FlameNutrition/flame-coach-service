package com.coach.flame.api.customer.request

data class CustomerRequest(
    val firstname: String?,
    val lastname: String?,
    val email: String?,
    val password: String?,
    val type: String?,
    val policy: Boolean?
)
