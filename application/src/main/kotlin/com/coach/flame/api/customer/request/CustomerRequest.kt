package com.coach.flame.api.customer.request

data class CustomerRequest(
    val firstname: String?,
    val lastname: String?,
    val email: String?,
    val password: String?,
    val type: String?,
    val registrationKey: String?,
    val policy: Boolean?,
) {

    override fun toString(): String {
        return "CustomerRequest(" +
                "firstname=$firstname, " +
                "lastname=$lastname, " +
                "email=$email, " +
                "registrationKey=$registrationKey, " +
                "password=******, " +
                "type=$type, " +
                "policy=$policy" +
                ")"
    }
}

