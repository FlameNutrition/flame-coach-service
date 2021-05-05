package com.coach.flame.domain

data class GenderDto(
    val id: Long? = null,
    val genderCode: String,
    val externalValue: String,
) {
    override fun toString(): String {
        return "GenderDto(" +
                "genderCode='$genderCode', " +
                "externalValue='$externalValue'" +
                ")"
    }
}
