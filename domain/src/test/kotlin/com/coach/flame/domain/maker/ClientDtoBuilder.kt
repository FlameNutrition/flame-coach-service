package com.coach.flame.domain.maker

import com.coach.flame.domain.ClientDto
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.MakeItEasy.with
import com.natpryce.makeiteasy.Maker
import java.time.LocalDateTime
import java.util.*

object ClientDtoBuilder {

    private val MAKER: Maker<ClientDto> = an(ClientDtoMaker.ClientDto)

    fun makerWithLoginInfo(): Maker<ClientDto> {
        val loginInfo = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()),
                with(LoginInfoDtoMaker.token, UUID.randomUUID()))
            .make()

        return MAKER
            .but(with(ClientDtoMaker.loginInfo, loginInfo))
    }

    fun maker(): Maker<ClientDto> {
        return MAKER
    }

    fun default(): ClientDto {
        return maker().make()
    }

}
