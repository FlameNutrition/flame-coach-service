package com.coach.flame.jpa.entity

enum class MeasureConfig(
    val code: String,
    val value: String,
) {
    KG_CM("KG_CM", "Kg/cm"),
    LBS_IN("LBS_IN", "Lbs/in")
}
