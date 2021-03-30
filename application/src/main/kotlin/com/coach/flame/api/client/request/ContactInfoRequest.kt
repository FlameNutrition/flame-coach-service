package com.coach.flame.api.client.request

data class ContactInfoRequest(
    val firstName: String,
    val lastName: String,
    val phoneCode: String?,
    val phoneNumber: String?,
    val countryCode: String?
)
