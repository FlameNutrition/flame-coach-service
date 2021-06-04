package com.coach.flame.jpa.entity

import com.coach.flame.domain.maker.*
import com.coach.flame.jpa.entity.RegistrationInvite.Companion.toRegistrationInvite
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.RegistrationInviteBuilder
import com.coach.flame.jpa.entity.maker.RegistrationInviteMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class RegistrationInviteTest {

    @Test
    fun `test convert entity to dto all values`() {

        val sendDttm = LocalDateTime.now()
        val acceptedDttm = LocalDateTime.now()
        val coachDto = CoachBuilder.default()

        val registrationInvite = RegistrationInviteBuilder.maker()
            .but(with(RegistrationInviteMaker.coachProp, coachDto),
                with(RegistrationInviteMaker.sendToProp, "test@gmail.com"),
                with(RegistrationInviteMaker.registrationKeyProp, "HELLO_KEY"),
                with(RegistrationInviteMaker.sendDttmProp, sendDttm),
                with(RegistrationInviteMaker.acceptedDttmProp, acceptedDttm))
            .make()

        val dto = registrationInvite.toDto("http://localhost:8080/api")

        then(dto.id).isEqualTo(dto.id)
        then(dto.sender).isNotNull
        then(dto.sendTo).isEqualTo("test@gmail.com")
        then(dto.registrationKey).isEqualTo("HELLO_KEY")
        then(dto.registrationLink).isEqualTo("http://localhost:8080/api")
        then(dto.sendDttm).isEqualTo(sendDttm)
        then(dto.acceptedDttm).isEqualTo(acceptedDttm)

    }

    @Test
    fun `test convert dto to entity all values`() {

        val sendDttm = LocalDateTime.now()
        val acceptedDttm = LocalDateTime.now()

        val registrationInvite = RegistrationEmailDtoBuilder.maker()
            .but(with(RegistrationEmailDtoMaker.id, 200L),
                with(RegistrationEmailDtoMaker.sender, CoachDtoBuilder.makerWithLoginInfo().make()),
                with(RegistrationEmailDtoMaker.sendTo, "test@gmail.com"),
                with(RegistrationEmailDtoMaker.registrationKey, "HELLO_KEY"),
                with(RegistrationEmailDtoMaker.registrationLink, "http://localhost"),
                with(RegistrationEmailDtoMaker.sendDttm, sendDttm),
                with(RegistrationEmailDtoMaker.acceptedDttm, acceptedDttm))
            .make()

        val entity = registrationInvite.toRegistrationInvite()

        then(entity.id).isEqualTo(200L)
        then(entity.coach).isNotNull
        then(entity.sendTo).isEqualTo("test@gmail.com")
        then(entity.registrationKey).isEqualTo("HELLO_KEY")
        then(entity.sendDttm).isEqualTo(sendDttm)
        then(entity.acceptedDttm).isEqualTo(acceptedDttm)

    }

}
