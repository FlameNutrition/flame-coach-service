package com.coach.flame.testing.component.api.coach

import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.coach.flame.testing.assertion.http.ErrorAssert
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.every
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

class GetClientsCoachTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/coach/getClientsAccepted",
        httpMethod = RequestMethod.GET,
        parameters = ["identifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get clients from coach`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client0 = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()
            .toClient()
        val client1 = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()
            .toClient()
        val coach = coachMaker
            .but(
                with(CoachMaker.clients, mutableListOf(client0, client1)),
                with(CoachMaker.uuid, uuid)
            )
            .make()

        every { coachRepositoryMock.findByUuid(uuid) } returns coach

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

        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(uuid.toString())
        then(jsonResponse.getAsJsonArray("clientsCoach")).hasSize(2)

        val client0Result = jsonResponse.getAsJsonArray("clientsCoach")
            .find { it.asJsonObject.getAsJsonPrimitive("identifier").asString == client0.uuid.toString() }

        then(client0Result).isNotNull
        then(client0Result!!.asJsonObject.getAsJsonPrimitive("firstname").asString).isEqualTo(client0.firstName)
        then(client0Result.asJsonObject.getAsJsonPrimitive("lastname").asString).isEqualTo(client0.lastName)
        then(client0Result.asJsonObject.getAsJsonPrimitive("status").asString).isEqualTo(client0.clientStatus.name)
        then(client0Result.asJsonObject.getAsJsonPrimitive("email").asString).isEqualTo(client0.user.email)
        then(client0Result.asJsonObject.getAsJsonPrimitive("registrationDate").asString).isEqualTo(client0.registrationDate.toString())

    }

    @Test
    @LoadRequest(
        endpoint = "/api/coach/getClientsAccepted",
        httpMethod = RequestMethod.GET,
        parameters = ["identifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get clients from coach but occurred an internal exception`() {

        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")

        every { coachRepositoryMock.findByUuid(uuid) } throws Exception("Ops...something is wrong!")

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("InternalServerException.html")
            .hasErrorMessageTitle("InternalServerException")
            .hasErrorMessageDetail("This is an internal problem, please contact the admin system.")
            .hasErrorMessageStatus("500")
            .hasErrorMessageCode("9999")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()
    }

}
