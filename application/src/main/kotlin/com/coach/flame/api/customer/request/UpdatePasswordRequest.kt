package com.coach.flame.api.customer.request

data class UpdatePasswordRequest(
    val email: String,
    val oldPassword: String,
    val newPassword: String,
) {
    override fun toString(): String {
        return "UpdatePasswordRequest(" +
                "email=$email, " +
                "oldPassword=******, " +
                "newPassword=******" +
                ")"
    }
}
