package com.coach.flame.domain

data class CalculatorInputDto(
    val formula: String,
    val weight: Double,
    val height: Double,
    val age: Int,
    val sex: String,
    val weightUnit: String,
    val heightUnit: String,
    val pal: Int
)
