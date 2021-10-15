package com.coach.flame.calculators

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

internal class CaloriesConverterTest {

    private val caloriesConverter: CaloriesConverter = CaloriesConverter()

    @Test
    fun `test calories converter`() {

        then(caloriesConverter.convertKilocalories(1850.4, CalculatorService.Unit.KILOCALORIES)).isEqualTo(1850.4)
        then(caloriesConverter.convertKilocalories(1850.4, CalculatorService.Unit.CALORIES)).isEqualTo(1850400.0)
        then(caloriesConverter.convertKilocalories(1850.4, CalculatorService.Unit.JOULES)).isEqualTo(7742073.600000001)

    }

}


