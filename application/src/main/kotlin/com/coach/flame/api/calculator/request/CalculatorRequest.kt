package com.coach.flame.api.calculator.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

class CalculatorRequest {

    @field:NotNull
    @field:Positive
    var height: Double? = null

    @field:NotNull
    @field:Positive
    var weight: Double? = null

    @field:NotNull
    @field:NotBlank
    var sex: String? = null

    @field:NotNull
    @field:Positive
    var age: Int? = null

    var caloriesUnit: String = "KILOCALORIES"

    var pal: Int? = null

    var unit: String = "KG/CM"

    override fun toString(): String {
        return "CalculatorRequest(" +
                "height=$height, " +
                "weight=$weight, " +
                "sex=$sex, " +
                "age=$age, " +
                "caloriesUnit=$caloriesUnit, " +
                "pal=$pal, " +
                "unit='$unit'" +
                ")"
    }
}
