package com.coach.flame.jpa.entity

import com.coach.flame.domain.maker.MeasureWeightDtoBuilder
import com.coach.flame.domain.maker.MeasureWeightDtoMaker
import com.coach.flame.jpa.entity.ClientMeasureWeight.Companion.toClientMeasureWeight
import com.coach.flame.jpa.entity.maker.*
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class ClientMeasureWeightTest {

    @Test
    fun `test convert client measure weight to dto all values`() {

        val measureWeight = ClientMeasureWeightBuilder.default()

        val dto = measureWeight.toDto()

        then(dto.id).isEqualTo(measureWeight.id)
        then(dto.value).isEqualTo(measureWeight.weight)
        then(dto.date).isEqualTo(measureWeight.measureDate)

    }

    @Test
    fun `test convert client measure weight dto to entity all values`() {

        val measureWeightDto = MeasureWeightDtoBuilder.maker()
            .but(with(MeasureWeightDtoMaker.id, 100L))
            .make()

        val entity = measureWeightDto.toClientMeasureWeight()

        then(entity.id).isEqualTo(measureWeightDto.id)
        then(entity.measureDate).isEqualTo(measureWeightDto.date)
        then(entity.weight).isEqualTo(measureWeightDto.value)

    }

}
