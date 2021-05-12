package com.coach.flame.domain

import com.coach.flame.domain.maker.LoginInfoDtoBuilder
import com.coach.flame.domain.maker.LoginInfoDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class LoginInfoDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.userId, 100L),
                with(LoginInfoDtoMaker.sessionId, 200L))
            .make()

        then(dto.toString())
            .contains("LoginInfoDto(")
            .doesNotContain("userId=100")
            .doesNotContain("sessionId=200")

    }

    @Test
    fun `test toString() without password info field`() {

        val dto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.password, "hello"))
            .make()

        then(dto.toString())
            .contains("LoginInfoDto(")
            .contains("password=")
            .contains("'*****'")
            .doesNotContain("hello=")

    }

}
