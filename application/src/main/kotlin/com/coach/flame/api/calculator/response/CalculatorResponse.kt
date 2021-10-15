package com.coach.flame.api.calculator.response

data class CalculatorResponse(
    val result: Double,
    val unit: String,
    val perUnit: String = "day"
) {
    override fun toString(): String {
        return "CalculatorResponse(" +
                "result=$result, " +
                "unit='$unit', " +
                "perUnit='$perUnit'" +
                ")"
    }
}
