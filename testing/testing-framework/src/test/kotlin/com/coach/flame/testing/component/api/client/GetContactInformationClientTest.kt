package com.coach.flame.testing.component.api.client

import com.coach.flame.jpa.entity.*
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

class GetContactInformationClientTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/client/getContactInformation",
        httpMethod = RequestMethod.GET,
        headers = ["clientIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get contact information for client`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuid),
                with(ClientMaker.firstname, "Nuno"),
                with(ClientMaker.lastname, "Neves"),
                with(ClientMaker.phoneCode, "+44"),
                with(ClientMaker.phoneNumber, "22444555664"),
                with(ClientMaker.country, CountryBuilder.maker()
                    .but(with(CountryMaker.countryCode, "PT"),
                        with(CountryMaker.externalValue, "Portugal"))
                    .make()))
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

        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("firstName").asString).isEqualTo("Nuno")
        then(jsonResponse.getAsJsonPrimitive("lastName").asString).isEqualTo("Neves")
        then(jsonResponse.getAsJsonPrimitive("phoneCode").asString).isEqualTo("+44")
        then(jsonResponse.getAsJsonPrimitive("phoneNumber").asString).isEqualTo("22444555664")
        then(jsonResponse.getAsJsonObject("country").get("code").asString).isEqualTo("PT")
        then(jsonResponse.getAsJsonObject("country").get("value").asString).isEqualTo("Portugal")

    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/getContactInformation",
        httpMethod = RequestMethod.GET,
        headers = ["clientIdentifier:e59343bc-"]
    )
    fun `test get contact information for client using invalid uuid`() {

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
        endpoint = "/api/client/getContactInformation",
        httpMethod = RequestMethod.GET,
        headers = ["clientIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get contact information for client without unmandatory param request`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuid),
                with(ClientMaker.firstname, "Nuno"),
                with(ClientMaker.lastname, "Neves"),
                with(ClientMaker.phoneCode, null as String?),
                with(ClientMaker.phoneNumber, null as String?),
                with(ClientMaker.country, null as CountryConfig?))
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

        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("firstName").asString).isEqualTo("Nuno")
        then(jsonResponse.getAsJsonPrimitive("lastName").asString).isEqualTo("Neves")
        then(jsonResponse.get("phoneCode").isJsonNull).isTrue
        then(jsonResponse.get("phoneNumber").isJsonNull).isTrue
        then(jsonResponse.get("country").isJsonNull).isTrue

    }

}
