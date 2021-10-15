package com.coach.flame.calculators

import com.coach.flame.domain.FormulaInputDto
import com.coach.flame.domain.FormulaInputDto.*
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class HarrisBenedictFormulaTest {

    private val harrisBenedictFormula = HarrisBenedictFormula()

    @ParameterizedTest(name = "[{index}] - Test Harris Benedict formula: WeightUnit: {0}, HeightUnit: {1}, Sex: {5}")
    @MethodSource("harrisBenedictWithoutPalFormulaParams")
    fun `test Harris Benedict formula without`(
        weightUnit: WeightUnit,
        heightUnit: HeightUnit,
        weight: Double,
        height: Double,
        age: Int,
        sex: Sex,
        tdeeExpected: Double,
        bmrExpected: Double
    ) {

        val formulaInputDto = FormulaInputDto(
            weight = weight, height = height, weightUnit = weightUnit,
            heightUnit = heightUnit, age = age, sex = sex
        )

        val result = harrisBenedictFormula.result(formulaInputDto)

        then(result.bmr).isEqualTo(bmrExpected)
        then(result.tdee).isEqualTo(tdeeExpected)

    }

    @ParameterizedTest(name = "[{index}] - Test Harris Benedict formula (pal): WeightUnit: {0}, HeightUnit: {1}, Sex: {5}")
    @MethodSource("harrisBenedictPalFormulaParams")
    fun `test Harris Benedict formula without`(
        weightUnit: WeightUnit,
        heightUnit: HeightUnit,
        weight: Double,
        height: Double,
        age: Int,
        sex: Sex,
        pal: Pal,
        tdeeExpected: Double,
        bmrExpected: Double
    ) {

        val formulaInputDto = FormulaInputDto(
            weight = weight, height = height, weightUnit = weightUnit,
            heightUnit = heightUnit, age = age, sex = sex, pal = pal
        )

        val result = harrisBenedictFormula.result(formulaInputDto)

        then(result.bmr).isEqualTo(bmrExpected)
        then(result.tdee).isEqualTo(tdeeExpected)

    }

    companion object {
        @JvmStatic
        fun harrisBenedictWithoutPalFormulaParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(WeightUnit.KG, HeightUnit.CM, 70, 175, 31, Sex.FEMALE, 0.0, 1477.75),
                Arguments.of(WeightUnit.KG, HeightUnit.CM, 70, 175, 31, Sex.MALE, 0.0, 1643.75),

                Arguments.of(WeightUnit.LB, HeightUnit.IN, 154.3, 68.89, 31, Sex.FEMALE, 0.0, 1504.29),
                Arguments.of(WeightUnit.LB, HeightUnit.IN, 154.3, 68.89, 31, Sex.MALE, 0.0, 1688.0),
            )
        }

        @JvmStatic
        fun harrisBenedictPalFormulaParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    WeightUnit.KG, HeightUnit.CM, 70, 175, 31, Sex.FEMALE,
                    Pal.LEVEL_0_SEDENTARY, 1773.3, 1477.75
                ),
                Arguments.of(
                    WeightUnit.KG, HeightUnit.CM, 70, 175, 31, Sex.FEMALE,
                    Pal.LEVEL_1_LIGHT_EXERCISE, 2031.91, 1477.75
                ),
                Arguments.of(
                    WeightUnit.KG, HeightUnit.CM, 70, 175, 31, Sex.FEMALE,
                    Pal.LEVEL_2_MODERATE_EXERCISE, 2290.51, 1477.75
                ),
                Arguments.of(
                    WeightUnit.KG, HeightUnit.CM, 70, 175, 31, Sex.FEMALE,
                    Pal.LEVEL_3_HARD_EXERCISE, 2549.12, 1477.75
                ),
                Arguments.of(
                    WeightUnit.KG, HeightUnit.CM, 70, 175, 31, Sex.FEMALE,
                    Pal.LEVEL_4_PHYSICAL_JOB_HARD_EXERCISE, 2807.72, 1477.75
                ),
                Arguments.of(
                    WeightUnit.KG, HeightUnit.CM, 70, 175, 31, Sex.FEMALE,
                    Pal.LEVEL_5_PROFFESSIONAL_ATHLETE, 2807.72, 1477.75
                ),
            )
        }
    }
}

