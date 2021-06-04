package com.coach.flame.jpa.repository

import com.coach.flame.jpa.AbstractHelperTest
import com.coach.flame.jpa.entity.CountryConfig
import com.coach.flame.jpa.entity.GenderConfig
import com.coach.flame.jpa.entity.maker.*
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RegistrationInviteRepositoryTest : AbstractHelperTest() {

    @Test
    fun `test create simple registration invite`() {

        // COACH
        val coach = getCoachRepository().saveAndFlush(CoachBuilder.maker()
            .but(with(CoachMaker.clientType, coachType),
                with(CoachMaker.user, userMaker.make()),
                with(CoachMaker.userSession, userSessionMaker.make())
            ).make())

        val now = LocalDateTime.now()
        val accepted = now.plusHours(3)

        val registrationInvite = getRegistrationInviteRepository()
            .saveAndFlush(RegistrationInviteBuilder.maker()
                .but(with(RegistrationInviteMaker.coachProp, coach),
                    with(RegistrationInviteMaker.sendDttmProp, now),
                    with(RegistrationInviteMaker.registrationKeyProp, "KEY"),
                    with(RegistrationInviteMaker.sendToProp, "test@gmail.com"),
                    with(RegistrationInviteMaker.acceptedDttmProp, accepted))
                .make())

        then(registrationInvite.id).isNotNull
        then(registrationInvite.coach).isNotNull
        then(registrationInvite.sendDttm).isEqualTo(now)
        then(registrationInvite.acceptedDttm).isEqualTo(accepted)
        then(registrationInvite.registrationKey).isEqualTo("KEY")
        then(registrationInvite.sendTo).isEqualTo("test@gmail.com")

    }

}
