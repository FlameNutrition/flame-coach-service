package com.coach.flame.domain

data class LookingForCoachDto(
    val isEnable: Boolean = false,
    val description: String? = null
) {

    override fun toString(): String {
        return "LookingForCoachDto(" +
                "isEnable=$isEnable, " +
                "description=$description" +
                ")"
    }
}