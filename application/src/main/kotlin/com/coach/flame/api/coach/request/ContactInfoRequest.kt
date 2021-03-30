package com.coach.flame.api.coach.request

data class ContactInfoRequest(
    val firstName: String,
    val lastName: String,
    val phoneCode: String?,
    val phoneNumber: String?,
    val countryCode: String?,
)
