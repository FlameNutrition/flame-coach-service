package com.coach.flame.testing.component.api.client

import com.coach.flame.jpa.entity.maker.*
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

class RegistrationInviteTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/client/invite",
        httpMethod = RequestMethod.POST,
        parameters = ["coachIdentifier:e59343bc-6563-4488-a77e-112e886c57ae", "clientEmail:test@gmail.com"]
    )
    fun `test send registration invite to client`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuid))
            .make()

        mockCoachRepository.findByUuid(uuid, coach)
        mockClientRepository.findByUserEmailIs("test@gmail.com", null)
        mockJavaMailSender.sendEmail()
        mockRegistrationInviteRepository.save()

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        then(jsonResponse.getAsJsonPrimitive("coachIdentifier").asString).isEqualTo("e59343bc-6563-4488-a77e-112e886c57ae")
        then(jsonResponse.getAsJsonPrimitive("registrationLink").asString).contains(
            listOf(
                "https://app.mock.flamenutrition.co.uk/register",
                "?registrationKey=",
                "&email=test@gmail.com"
            )
        )
        then(jsonResponse.getAsJsonPrimitive("registrationKey").asString).isNotNull
        then(jsonResponse.getAsJsonPrimitive("registrationInvite").asBoolean).isTrue

    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/invite",
        httpMethod = RequestMethod.POST,
        parameters = ["coachIdentifier:e59343bc-6563-4488-a77e-112e886c57ae", "clientEmail:test@gmail.com"]
    )
    fun `test send enrollment init invite to client`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.user, UserBuilder.maker()
                .but(with(UserMaker.email, "test@gmail.com"))
                .make()))
            .make()
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuid))
            .make()

        mockCoachRepository.findByUuid(uuid, coach)
        mockClientRepository.findByUserEmailIs("test@gmail.com", client)
        mockClientRepository.findByUuid(client.uuid, client)
        mockClientRepository.save()

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        then(jsonResponse.getAsJsonPrimitive("coachIdentifier").asString).isEqualTo("e59343bc-6563-4488-a77e-112e886c57ae")
        then(jsonResponse.getAsJsonPrimitive("registrationInvite").asBoolean).isFalse
        then(jsonResponse.getAsJsonPrimitive("clientStatus").asString).isEqualTo("PENDING")

    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/invite",
        httpMethod = RequestMethod.POST,
        parameters = ["coachIdentifier:e59343bc-6563-4488-a77e-112e886c57ae", "clientEmail:test@gmail.com"]
    )
    fun `test send registration invite to client but email failed`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuid))
            .make()

        mockCoachRepository.findByUuid(uuid, coach)
        mockClientRepository.findByUserEmailIs("test@gmail.com", null)
        mockJavaMailSender.sendEmail(RuntimeException("EMAIL PROBLEM!"))
        mockRegistrationInviteRepository.save()

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("MailException.html")
        thenErrorMessageTitle(body).isEqualTo("MailException")
        thenErrorMessageDetail(body).isEqualTo("Something happened when trying to send the registration link.")
        thenErrorMessageStatus(body).isEqualTo("500")
        thenErrorCode(body).isEqualTo("7000")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()
    }

}
