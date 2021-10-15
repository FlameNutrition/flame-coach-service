package com.coach.flame.calculators

import com.coach.flame.domain.CalculatorOutputDto
import com.coach.flame.domain.FormulaInputDto
import org.springframework.stereotype.Service

/**
 * Service to retrieve the Basal Metabolic Rate and Total Daily Energy Expenditure
 * based on a [Formula] and other input parameters [FormulaInputDto]. Use this component if you need calculate the BMR or TDEE.
 *
 * @author Nuno Bento
 */
@Service
class CalculatorService(
    private val harrisBenedictFormula: HarrisBenedictFormula,
    private val caloriesConverter: CaloriesConverter
) {

    enum class Unit {
        KILOCALORIES, CALORIES, JOULES
    }

    fun calculate(formula: String, unit: Unit, formulaInputDto: FormulaInputDto): CalculatorOutputDto {

        val outputFormula = when (formula) {
            HarrisBenedictFormula.NAME -> {
                harrisBenedictFormula.result(formulaInputDto)
            }
            else -> throw UnsupportedFormulaException("$formula formula is not supported.")
        }

        val bmr = caloriesConverter.convertKilocalories(outputFormula.bmr, unit)
        val tdee = caloriesConverter.convertKilocalories(outputFormula.tdee, unit)

        return CalculatorOutputDto(bmr, tdee)
    }

}
