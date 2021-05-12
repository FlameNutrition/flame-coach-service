package com.coach.flame.measures

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class WeightConverterTest {

    @Test
    fun `test lbs converter`() {

        then(WeightConverter.convertWeightToLbs(1f, WeightConverter.WeightType.KGS))
            .isEqualTo(2.2f)
        then(WeightConverter.convertWeightToLbs(70.4f, WeightConverter.WeightType.KGS))
            .isEqualTo(155.2f)
        then(WeightConverter.convertWeightToLbs(1f, WeightConverter.WeightType.LBS))
            .isEqualTo(1f)

    }

    @Test
    fun `test kgs converter`() {

        then(WeightConverter.convertWeightToKgs(2.2046f, WeightConverter.WeightType.LBS))
            .isEqualTo(1f)
        then(WeightConverter.convertWeightToKgs(155.2054f, WeightConverter.WeightType.LBS))
            .isEqualTo(70.4f)
        then(WeightConverter.convertWeightToKgs(2.2046f, WeightConverter.WeightType.KGS))
            .isEqualTo(2.2046f)

    }

}
