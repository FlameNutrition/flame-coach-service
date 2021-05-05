package com.coach.flame.jpa.entity

import com.coach.flame.domain.maker.LoginInfoDtoBuilder
import com.coach.flame.domain.maker.LoginInfoDtoMaker
import com.coach.flame.jpa.entity.User.Companion.toUser
import com.coach.flame.jpa.entity.maker.*
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.util.*

class UserTest {

    @Test
    fun `test convert user to dto all values`() {

        val entity = UserBuilder.default()

        val dto = entity.toDto()

        then(dto.userId).isEqualTo(entity.id)
        then(dto.sessionId).isEqualTo(entity.userSession.id)
        then(dto.username).isEqualTo(entity.email)
        then(dto.password).isEqualTo(entity.password)
        then(dto.keyDecrypt).isEqualTo(entity.keyDecrypt)
        then(dto.expirationDate).isEqualTo(entity.userSession.expirationDate)
        then(dto.token).isEqualTo(entity.userSession.token)
    }

    @Test
    fun `test convert user to entity all values`() {

        val dto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
            .make()

        val entity = dto.toUser()

        then(entity.id).isEqualTo(dto.userId)
        then(entity.userSession).isNotNull
        then(entity.email).isEqualTo(dto.username)
        then(entity.password).isEqualTo(dto.password)
        then(entity.keyDecrypt).isEqualTo(dto.keyDecrypt)

    }

    @Test
    fun `test convert user to entity illegal args`() {

        val dto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.keyDecrypt, null as String?))
            .make()

        val exception = catchThrowable { dto.toUser() }

        then(exception).isInstanceOf(IllegalArgumentException::class.java)
        then(exception).hasMessageContaining("keyDecrypt can not be null")

    }

}
