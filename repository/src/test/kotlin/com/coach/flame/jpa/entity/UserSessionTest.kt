package com.coach.flame.jpa.entity

import com.coach.flame.domain.maker.LoginInfoDtoBuilder
import com.coach.flame.domain.maker.LoginInfoDtoMaker
import com.coach.flame.jpa.entity.UserSession.Companion.toUserSession
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.util.*

class UserSessionTest {

    @Test
    fun `test convert user session to entity all values`() {

        val dto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
            .make()

        val entity = dto.toUserSession()

        then(entity.id).isEqualTo(dto.userId)
        then(entity.token).isNotNull
        then(entity.expirationDate).isEqualTo(dto.expirationDate)

    }

    @Test
    fun `test convert user to entity illegal args`() {

        val dto0 = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.token, null as UUID?))
            .make()
        val dto1 = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.expirationDate, null as LocalDateTime?),
                with(LoginInfoDtoMaker.token, UUID.randomUUID()))
            .make()

        val exception0 = catchThrowable { dto0.toUserSession() }
        val exception1 = catchThrowable { dto1.toUserSession() }

        then(exception0).isInstanceOf(IllegalArgumentException::class.java)
        then(exception0).hasMessageContaining("token can not be null")

        then(exception1).isInstanceOf(IllegalArgumentException::class.java)
        then(exception1).hasMessageContaining("expirationDate can not be null")

    }

}
