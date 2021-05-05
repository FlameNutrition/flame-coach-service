package com.coach.flame.testing.component.api.client

import com.coach.flame.failure.domain.ErrorCode
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
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

class UpdatePersonalDataClientTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/updatePersonalDataClient.json",
        endpoint = "/api/client/updatePersonalData",
        httpMethod = RequestMethod.POST,
        headers = ["clientIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test update personal data for client`() {

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
        val clientCapture = slot<Client>()

        every { clientRepositoryMock.findByUuid(uuid) } returns client
        every { genderConfigCacheMock.getValue("F") } returns Optional.of(GenderBuilder.maker()
            .but(with(GenderMaker.genderCode, "F"),
                with(GenderMaker.externalValue, "Female"))
            .make())
        every { clientRepositoryMock.save(capture(clientCapture)) } answers { clientCapture.captured }

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

        then(jsonResponse.getAsJsonPrimitive("weight").asFloat).isEqualTo(143.3f)
        then(jsonResponse.getAsJsonPrimitive("height").asFloat).isEqualTo(167f)
        then(jsonResponse.getAsJsonObject("gender").asJsonObject.get("value").asString).isEqualTo("Female")
        then(jsonResponse.getAsJsonObject("gender").asJsonObject.get("code").asString).isEqualTo("F")
        then(jsonResponse.getAsJsonObject("measureType").asJsonObject.get("value").asString).isEqualTo("Lbs/in")
        then(jsonResponse.getAsJsonObject("measureType").asJsonObject.get("code").asString).isEqualTo("LBS_IN")

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/updateContactInfoClient.json",
        endpoint = "/api/client/updatePersonalData",
        httpMethod = RequestMethod.POST,
        headers = ["clientIdentifier:e59343bc-"]
    )
    fun `test update personal data for client using invalid uuid`() {

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
        pathOfRequest = "requests/component/client/updatePersonalDataWithNullValuesClient.json",
        endpoint = "/api/client/updatePersonalData",
        httpMethod = RequestMethod.POST,
        headers = ["clientIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test update personal data for client without unmandatory param request`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuid),
                with(ClientMaker.weight, 80.5f),
                with(ClientMaker.height, 1.75f),
                with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.measureConfig, MeasureConfig.KG_CM))
            .make()
        val clientCapture = slot<Client>()

        every { clientRepositoryMock.findByUuid(uuid) } returns client
        every { clientRepositoryMock.save(capture(clientCapture)) } answers { clientCapture.captured }

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

        then(jsonResponse.getAsJsonPrimitive("weight").asFloat).isEqualTo(60.5f)
        then(jsonResponse.getAsJsonPrimitive("height").asFloat).isEqualTo(176f)
        then(jsonResponse.get("gender").isJsonNull).isTrue
        then(jsonResponse.getAsJsonObject("measureType").asJsonObject.get("value").asString).isEqualTo("Kg/cm")
        then(jsonResponse.getAsJsonObject("measureType").asJsonObject.get("code").asString).isEqualTo("KG_CM")

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/updatePersonalDataWithInvalidGenderClient.json",
        endpoint = "/api/client/updatePersonalData",
        httpMethod = RequestMethod.POST,
        headers = ["clientIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test update personal data for client with invalid gender`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuid),
                with(ClientMaker.weight, 80.5f),
                with(ClientMaker.height, 1.75f),
                with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.measureConfig, MeasureConfig.KG_CM))
            .make()
        val clientCapture = slot<Client>()

        every { clientRepositoryMock.findByUuid(uuid) } returns client
        every { genderConfigCacheMock.getValue("KILL") } returns Optional.empty()
        every { clientRepositoryMock.save(capture(clientCapture)) } answers { clientCapture.captured }

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

        thenErrorMessageType(body).endsWith("UnexpectedConfigException.html")
        thenErrorMessageTitle(body).isEqualTo("UnexpectedConfigException")
        thenErrorMessageDetail(body).isEqualTo("Gender Code: 'KILL' is not present in the system.")
        thenErrorMessageStatus(body).isEqualTo("500")
        thenErrorCode(body).isEqualTo("5001")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }


}
