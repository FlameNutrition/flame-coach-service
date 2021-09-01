package com.coach.flame.domain.maker

import com.coach.flame.domain.IncomeDto
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import com.coach.flame.domain.IncomeDto.IncomeStatus as IncomeDtoStatus

object IncomeDtoBuilder {

    private val MAKER: Maker<IncomeDto> = an(IncomeDtoMaker.IncomeDto)

    fun maker(): Maker<IncomeDto> {
        return MAKER
    }

    fun default(): IncomeDto {
        return maker().make()
    }

    class IncomeDtoMaker {
        companion object {

            val id: Property<IncomeDto, Long?> = Property.newProperty()
            val price: Property<IncomeDto, Float> = Property.newProperty()
            val status: Property<IncomeDto, IncomeDtoStatus> = Property.newProperty()

            val IncomeDto: Instantiator<IncomeDto> = Instantiator {
                val income = IncomeDto(
                    price = it.valueOf(price, 0.0f),
                    status = it.valueOf(status, IncomeDtoStatus.PENDING)
                )
                income.id = it.valueOf(id, null as Long?)

                income
            }
        }
    }
}
