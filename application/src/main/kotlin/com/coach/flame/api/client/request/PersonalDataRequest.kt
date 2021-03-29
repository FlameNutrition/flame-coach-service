package com.coach.flame.api.client.request

data class PersonalDataRequest(
    val measureTypeCode: String,
    val weight: Float,
    val height: Float,
    val genderCode: String?,
)
