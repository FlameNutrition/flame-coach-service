package com.coach.flame.domain.maker

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CoachDto
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import java.time.LocalDateTime
import java.util.*

object CoachDtoBuilder {

    private val MAKER: Maker<CoachDto> = an(CoachDtoMaker.CoachDto)

    fun makerWithLoginInfo(): Maker<CoachDto> {
        val loginInfo = LoginInfoDtoBuilder.maker()
            .but(MakeItEasy.with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()),
                MakeItEasy.with(LoginInfoDtoMaker.token, UUID.randomUUID()))
            .make()

        return MAKER
            .but(MakeItEasy.with(CoachDtoMaker.loginInfo, loginInfo))
    }

    fun maker(): Maker<CoachDto> {
        return MAKER
    }

    fun default(): CoachDto {
        return maker().make()
    }

}
