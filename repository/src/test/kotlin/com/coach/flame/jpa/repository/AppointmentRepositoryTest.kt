package com.coach.flame.jpa.repository

import com.coach.flame.domain.maker.AppointmentDtoBuilder
import com.coach.flame.domain.maker.AppointmentDtoMaker
import com.coach.flame.jpa.AbstractHelperTest
import com.coach.flame.jpa.entity.Appointment.Companion.toAppointment
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppointmentRepositoryTest : AbstractHelperTest() {

    @Test
    fun `test create appointment`() {

        val client = ClientBuilder.maker()
            .but(with(ClientMaker.clientType, clientType),
                with(ClientMaker.user, userMaker.make())
            ).make()

        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.clientType, clientType),
                with(CoachMaker.user, userMaker.make())
            ).make()

        getClientRepository().saveAndFlush(client)
        getCoachRepository().saveAndFlush(coach)

        entityManager.flush()
        entityManager.clear()

        val appointment = AppointmentDtoBuilder.maker()
            .but(with(AppointmentDtoMaker.client, client.toDto()),
                with(AppointmentDtoMaker.coach, coach.toDto()))
            .make()

        getAppointmentRepository().save(appointment.toAppointment())

    }

}
