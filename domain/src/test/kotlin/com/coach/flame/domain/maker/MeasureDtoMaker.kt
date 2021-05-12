package com.coach.flame.domain.maker

import com.coach.flame.domain.MeasureDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate

class MeasureDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<MeasureDto, Long?> = newProperty()
        val date: Property<MeasureDto, LocalDate> = newProperty()
        val value: Property<MeasureDto, Float> = newProperty()

        val MeasureDto: Instantiator<MeasureDto> = Instantiator {
            MeasureDto(
                id = it.valueOf(id, null as Long?),
                date = it.valueOf(date, LocalDate.now()),
                value = it.valueOf(value, fake.number().randomNumber().toFloat())
            )
        }
    }

}
