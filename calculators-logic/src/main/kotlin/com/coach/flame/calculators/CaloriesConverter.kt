package com.coach.flame.calculators

import org.springframework.stereotype.Component

@Component("caloriesConverter")
class CaloriesConverter {

    fun convertKilocalories(value: Double, convertTo: CalculatorService.Unit): Double {
        return when (convertTo) {
            CalculatorService.Unit.KILOCALORIES -> value
            CalculatorService.Unit.CALORIES -> value * 1000
            CalculatorService.Unit.JOULES -> value * 4184
        }
    }

}
