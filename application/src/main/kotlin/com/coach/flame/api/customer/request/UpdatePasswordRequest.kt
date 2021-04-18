package com.coach.flame.api.customer.request

data class UpdatePasswordRequest(
    val email: String,
    val oldPassword: String,
    val newPassword: String
)
