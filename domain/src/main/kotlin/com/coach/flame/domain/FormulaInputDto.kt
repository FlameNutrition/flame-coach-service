package com.coach.flame.domain

data class FormulaInputDto(
    val weight: Double,
    val height: Double,
    val age: Int,
    val sex: Sex,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val heightUnit: HeightUnit = HeightUnit.CM,
    val pal: Pal? = null
) {

    enum class WeightUnit {
        KG, LB
    }

    enum class HeightUnit {
        CM, IN
    }

    enum class Sex {
        MALE, FEMALE
    }

    enum class Pal(val value: Double) {
        LEVEL_0_SEDENTARY(1.2),
        LEVEL_1_LIGHT_EXERCISE(1.375),
        LEVEL_2_MODERATE_EXERCISE(1.55),
        LEVEL_3_HARD_EXERCISE(1.725),
        LEVEL_4_PHYSICAL_JOB_HARD_EXERCISE(1.9),
        LEVEL_5_PROFFESSIONAL_ATHLETE(1.9);

        companion object {
            fun getByLevel(level: Int): Pal {

                for (value in values()) {
                    if (value.ordinal == level) {
                        return value
                    }
                }

                throw IllegalArgumentException("Did not found the selected pal level")
            }
        }
    }

    override fun toString(): String {
        return "FormulaInputDto(" +
                "weight=$weight, " +
                "height=$height, " +
                "age=$age, " +
                "sex=$sex, " +
                "weightUnit=$weightUnit, " +
                "heightUnit=$heightUnit, " +
                "pal=$pal" +
                ")"
    }

}
