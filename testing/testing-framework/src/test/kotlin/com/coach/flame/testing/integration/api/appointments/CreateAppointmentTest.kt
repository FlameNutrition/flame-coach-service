package com.coach.flame.testing.integration.api.appointments

import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.coach.flame.testing.integration.base.BaseIntegrationTest
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

class CreateAppointmentTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client: Client
    private lateinit var coach: Coach

    @BeforeEach
    override fun setup() {
        super.setup()

        coach = coachRepository.saveAndFlush(CoachBuilder.maker()
            .but(with(CoachMaker.uuid, UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")),
                with(CoachMaker.clientType, coachType))
            .make())

        client = clientRepository.saveAndFlush(
            ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.identifier, UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")),
                with(ClientDtoMaker.coach, coach.toDto()))
            .make().toClient())


    }

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/create",
        httpMethod = RequestMethod.POST,
        pathOfRequest = "requests/component/appointments/createAppointment.json",
        parameters = [
            "coachIdentifier:e59343bc-6563-4488-a77e-112e886c57ae",
            "clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a"
        ]
    )
    fun `test create appointment`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonArray("appointments")).hasSize(1)

        val appointment1 = body.getAsJsonArray("appointments").first()

        then(appointment1.asJsonObject.getAsJsonPrimitive("dttmStarts").asString).isEqualTo("2021-07-14T03:52:52+01:00")
        then(appointment1.asJsonObject.getAsJsonPrimitive("dttmEnds").asString).isEqualTo("2021-07-14T05:52:52+01:00")
        then(appointment1.asJsonObject.getAsJsonPrimitive("price").asFloat).isEqualTo(156.5f)
        then(appointment1.asJsonObject.getAsJsonPrimitive("notes").asString).isEqualTo("This is my first appointment")
        then(appointment1.asJsonObject.getAsJsonObject("client").getAsJsonPrimitive("identifier").asString).isEqualTo(
            client.uuid.toString())
        then(appointment1.asJsonObject.getAsJsonObject("client").getAsJsonPrimitive("firstName").asString).isEqualTo(
            client.firstName)
        then(appointment1.asJsonObject.getAsJsonObject("client").getAsJsonPrimitive("lastName").asString).isEqualTo(
            client.lastName)
    }

}
