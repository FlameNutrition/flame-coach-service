package com.coach.flame.calculators

import com.coach.flame.domain.FormulaInputDto
import com.coach.flame.domain.FormulaOutputDto
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component("harrisBenedictFormula")
class HarrisBenedictFormula : Formula {

    companion object {
        private const val SCALE_FORMULA = 2
        const val NAME = "harris_benedict"
    }

    override fun result(formulaInput: FormulaInputDto): FormulaOutputDto {

        var tdee = 0.0
        val bmr = when (formulaInput.weightUnit) {
            FormulaInputDto.WeightUnit.KG -> {
                if (formulaInput.sex === FormulaInputDto.Sex.FEMALE) {
                    // women = 10 ⨉ weight (kg) + 6.25 ⨉ height (cm) – 5 ⨉ age (years) – 161
                    (10 * formulaInput.weight) + (6.25 * formulaInput.height) - (5 * formulaInput.age) - 161
                } else {
                    // men = 10 ⨉ weight (kg) + 6.25 ⨉ height (cm) – 5 ⨉ age (years) + 5
                    (10 * formulaInput.weight) + (6.25 * formulaInput.height) - (5 * formulaInput.age) + 5
                }
            }
            FormulaInputDto.WeightUnit.LB -> {
                if (formulaInput.sex === FormulaInputDto.Sex.FEMALE) {
                    // women = 655.1 + (4.35 ⨉ weight in pounds) + (4.7 ⨉ height in inches) − (4.7 ⨉ age in years)
                    655 + (4.35 * formulaInput.weight) + (4.7 * formulaInput.height) - (4.7 * formulaInput.age)
                } else {
                    // men = 66.47 + (6.24 ⨉ weight in pounds) + (12.7 ⨉ height in inches ) − (6.755 ⨉ age in years)
                    66 + (6.2 * formulaInput.weight) + (12.7 * formulaInput.height) - (6.76 * formulaInput.age)
                }
            }
        }

        if (formulaInput.pal != null) {
            tdee = bmr * formulaInput.pal!!.value
        }

        return FormulaOutputDto(
            BigDecimal(bmr).setScale(SCALE_FORMULA, RoundingMode.HALF_EVEN).toDouble(),
            BigDecimal(tdee).setScale(SCALE_FORMULA, RoundingMode.HALF_EVEN).toDouble()
        )
    }

}
