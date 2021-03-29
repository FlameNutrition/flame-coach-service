package com.coach.flame.api.client.response

data class PersonalDataResponse(
    val weight: Float,
    val height: Float,
    val gender: Config?,
    val measureType: Config,
)
