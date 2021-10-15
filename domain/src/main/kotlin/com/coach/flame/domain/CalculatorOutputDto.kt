package com.coach.flame.domain

data class CalculatorOutputDto(
    // Basal Metabolic Rate
    var bmr: Double,
    // Total Daily Energy Expenditure
    var tdee: Double
) {
    override fun toString(): String {
        return "CalculatorOutputDto(" +
                "TDEE=$tdee, " +
                "BMR=$bmr" +
                ")"
    }
}
