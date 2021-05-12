package com.coach.flame.measures

import kotlin.math.round

object WeightConverter {

    enum class WeightType {
        KGS, LBS
    }

    /**
     * Convert a value into [WeightType.LBS]
     *
     * @param value the value you want to convert
     * @param type the type of input value
     *
     * @return the value converted into [WeightType.LBS] or the same value if not found the right value type
     */
    fun convertWeightToLbs(value: Float, type: WeightType): Float {

        return when {
            type === WeightType.KGS -> round((value * 2.2046f) * 100.0f) / 100.0f
            else -> value
        }

    }

    /**
     * Convert a value into [WeightType.KGS]
     *
     * @param value the value you want to convert
     * @param type the type of input value
     *
     * @return the value converted into [WeightType.KGS] or the same value if not found the right value type
     */
    fun convertWeightToKgs(value: Float, type: WeightType): Float {

        return when {
            type === WeightType.LBS -> round((value / 2.2046f) * 100.0f) / 100.0f
            else -> value
        }

    }

}
