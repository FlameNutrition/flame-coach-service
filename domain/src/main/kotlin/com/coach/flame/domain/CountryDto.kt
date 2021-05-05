package com.coach.flame.domain

data class CountryDto(
    val id: Long? = null,
    val countryCode: String,
    val externalValue: String,
) {
    override fun toString(): String {
        return "CountryDto(" +
                "countryCode='$countryCode', " +
                "externalValue='$externalValue'" +
                ")"
    }
}
