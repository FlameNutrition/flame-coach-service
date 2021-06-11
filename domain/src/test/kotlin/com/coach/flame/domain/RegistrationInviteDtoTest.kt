package com.coach.flame.domain

import com.coach.flame.domain.maker.RegistrationInviteDtoBuilder
import com.coach.flame.domain.maker.RegistrationInviteDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class RegistrationInviteDtoTest {

    @Test
    fun `test toString() without id field`() {

        val dto = RegistrationInviteDtoBuilder.maker()
            .but(with(RegistrationInviteDtoMaker.id, 100L))
            .make()

        then(dto.toString())
            .contains("RegistrationInviteDto(")
            .doesNotContain("id=100")

    }

}
