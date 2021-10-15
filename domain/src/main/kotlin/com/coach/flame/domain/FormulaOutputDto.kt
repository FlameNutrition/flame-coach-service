package com.coach.flame.domain

data class FormulaOutputDto(
    // Basal Metabolic Rate
    var bmr: Double,
    // Total Daily Energy Expenditure
    var tdee: Double
) {
    override fun toString(): String {
        return "FormulaOutputDto(" +
                "TDEE=$tdee, " +
                "BMR=$bmr" +
                ")"
    }
}
