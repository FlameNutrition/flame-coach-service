package com.coach.flame.domain.maker

import com.coach.flame.domain.MeasureDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong

object MeasureDtoBuilder {

    private val atomicId: AtomicLong = AtomicLong(1)

    private val MAKER: Maker<MeasureDto> = an(MeasureDtoMaker.MeasureDto)

    fun makerWithId(): Maker<MeasureDto> {
        return maker()
            .but(MakeItEasy.with(MeasureDtoMaker.id, atomicId.getAndIncrement()))
    }

    fun maker(): Maker<MeasureDto> {
        return MAKER
    }

    fun default(): MeasureDto {
        return maker().make()
    }

}

class MeasureDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<MeasureDto, Long?> = Property.newProperty()
        val date: Property<MeasureDto, LocalDate> = Property.newProperty()
        val value: Property<MeasureDto, Float> = Property.newProperty()

        val MeasureDto: Instantiator<MeasureDto> = Instantiator {
            MeasureDto(
                id = it.valueOf(id, null as Long?),
                date = it.valueOf(date, LocalDate.now()),
                value = it.valueOf(value, fake.number().randomNumber().toFloat())
            )
        }
    }

}
