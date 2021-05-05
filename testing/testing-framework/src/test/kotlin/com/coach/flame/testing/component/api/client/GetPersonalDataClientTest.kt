package com.coach.flame.testing.component.api.client

import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.GenderBuilder
import com.coach.flame.jpa.entity.maker.GenderMaker
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

class GetPersonalDataClientTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/client/getPersonalData",
        httpMethod = RequestMethod.GET,
        headers = ["clientIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get personal data for client`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuid),
                with(ClientMaker.weight, 80.5f),
                with(ClientMaker.height, 1.75f),
                with(ClientMaker.gender, GenderBuilder.maker()
                    .but(with(GenderMaker.externalValue, "Male"),
                        with(GenderMaker.genderCode, "M"))
                    .make()),
                with(ClientMaker.measureConfig, MeasureConfig.KG_CM))
            .make()

        every { clientRepositoryMock.findByUuid(uuid) } returns client

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

        then(jsonResponse.getAsJsonPrimitive("weight").asFloat).isEqualTo(80.5f)
        then(jsonResponse.getAsJsonPrimitive("height").asFloat).isEqualTo(1.75f)
        then(jsonResponse.getAsJsonObject("gender").asJsonObject.get("value").asString).isEqualTo("Male")
        then(jsonResponse.getAsJsonObject("gender").asJsonObject.get("code").asString).isEqualTo("M")
        then(jsonResponse.getAsJsonObject("measureType").asJsonObject.get("value").asString).isEqualTo("Kg/cm")
        then(jsonResponse.getAsJsonObject("measureType").asJsonObject.get("code").asString).isEqualTo("KG_CM")

    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/getPersonalData",
        httpMethod = RequestMethod.GET,
        headers = ["clientIdentifier:e59343bc-"]
    )
    fun `test get personal data for client using invalid uuid`() {

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

        thenErrorMessageType(body).endsWith("InternalServerException.html")
        thenErrorMessageTitle(body).isEqualTo("InternalServerException")
        thenErrorMessageDetail(body).isEqualTo("This is an internal problem, please contact the admin system")
        thenErrorMessageStatus(body).isEqualTo("500")
        thenErrorCode(body).isEqualTo("9999")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/getPersonalData",
        httpMethod = RequestMethod.GET,
        headers = ["clientIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get personal data for client without unmandatory param request`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuid),
                with(ClientMaker.weight, 0.0f),
                with(ClientMaker.height, 0.0f),
                with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.measureConfig, MeasureConfig.LBS_IN))
            .make()

        every { clientRepositoryMock.findByUuid(uuid) } returns client

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

        then(jsonResponse.getAsJsonPrimitive("weight").asFloat).isEqualTo(0.0f)
        then(jsonResponse.getAsJsonPrimitive("height").asFloat).isEqualTo(0.0f)
        then(jsonResponse.get("gender").isJsonNull).isTrue
        then(jsonResponse.getAsJsonObject("measureType").asJsonObject.get("value").asString).isEqualTo("Lbs/in")
        then(jsonResponse.getAsJsonObject("measureType").asJsonObject.get("code").asString).isEqualTo("LBS_IN")


    }

}
