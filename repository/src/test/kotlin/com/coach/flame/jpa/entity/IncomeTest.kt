package com.coach.flame.jpa.entity

import com.coach.flame.domain.IncomeDto
import com.coach.flame.domain.maker.IncomeDtoBuilder
import com.coach.flame.domain.maker.IncomeDtoMaker
import com.coach.flame.jpa.entity.Income.Companion.toIncome
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class IncomeTest {

    @Test
    fun `test convert income to dto all values`() {

        val income = Income()
            .apply {
                id = 200L
                price = 500.4f
                status = "PENDING"
            }

        val dto = income.toDto()

        then(dto.price).isEqualTo(500.4f)
        then(dto.status).isEqualTo(IncomeDto.IncomeStatus.PENDING)

    }


    @Test
    fun `test convert dto to entity all values`() {

        val income = IncomeDtoBuilder.maker()
            .but(with(IncomeDtoMaker.id, 200L),
                with(IncomeDtoMaker.price, 760.42f),
                with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.REJECTED))
            .make()

        val entity = income.toIncome()

        then(entity.id).isEqualTo(200L)
        then(entity.price).isEqualTo(760.42f)
        then(entity.status).isEqualTo("REJECTED")

    }

}
