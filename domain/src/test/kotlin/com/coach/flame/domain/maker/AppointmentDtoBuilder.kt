package com.coach.flame.domain.maker

import com.coach.flame.domain.AppointmentDto
import com.coach.flame.domain.ClientDto
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.MakeItEasy.with
import com.natpryce.makeiteasy.Maker
import java.time.LocalDateTime
import java.util.*

object AppointmentDtoBuilder {

    private val MAKER: Maker<AppointmentDto> = an(AppointmentDtoMaker.AppointmentDto)

    fun maker(): Maker<AppointmentDto> {
        return MAKER
    }

    fun default(): AppointmentDto {
        return maker().make()
    }

}
