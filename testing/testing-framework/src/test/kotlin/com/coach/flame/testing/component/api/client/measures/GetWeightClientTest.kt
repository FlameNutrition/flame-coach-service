package com.coach.flame.testing.component.api.client.measures

import com.coach.flame.domain.MeasureTypeDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.domain.maker.MeasureDtoBuilder
import com.coach.flame.domain.maker.MeasureDtoMaker
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.testing.assertion.http.ErrorAssert
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.component.base.mock.MockClientRepository
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

class GetWeightClientTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/client/measures/weight/get",
        httpMethod = RequestMethod.GET,
        parameters = ["clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a"]
    )
    fun `test get weights from client`() {

        // given
        val uuid = UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")

        val weight0 = MeasureDtoBuilder.maker()
            .but(
                with(MeasureDtoMaker.id, 100L),
                with(MeasureDtoMaker.value, 80.5f)
            )
            .make()
        val weight1 = MeasureDtoBuilder.maker()
            .but(
                with(MeasureDtoMaker.id, 200L),
                with(MeasureDtoMaker.value, 70.5f)
            )
            .make()
        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(
                with(ClientDtoMaker.listOfWeights, mutableListOf(weight0, weight1))
            )
            .make()
            .toClient()

        mockClientRepository
            .mock(MockClientRepository.FIND_BY_UUID)
            .params(mapOf(Pair("uuid", uuid)))
            .returns { client }

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

        then(jsonResponse.getAsJsonArray("weights")).hasSize(2)

    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/measures/weight/get",
        httpMethod = RequestMethod.GET,
        parameters = ["clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a"]
    )
    fun `test get weights from client with measure type lbs`() {

        // given
        val uuid = UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")

        val weight0 = MeasureDtoBuilder.maker()
            .but(
                with(MeasureDtoMaker.id, 100L),
                with(MeasureDtoMaker.value, 81.87f)
            )
            .make()
        val weight1 = MeasureDtoBuilder.maker()
            .but(
                with(MeasureDtoMaker.id, 200L),
                with(MeasureDtoMaker.value, 86.32f)
            )
            .make()
        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(
                with(ClientDtoMaker.listOfWeights, mutableListOf(weight0, weight1)),
                with(ClientDtoMaker.measureType, MeasureTypeDto.LBS_IN)
            )
            .make()
            .toClient()

        mockClientRepository
            .mock(MockClientRepository.FIND_BY_UUID)
            .params(mapOf(Pair("uuid", uuid)))
            .returns { client }

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

        then(jsonResponse.getAsJsonArray("weights")).hasSize(2)

        val weight100 = jsonResponse.getAsJsonArray("weights")
            .find { it.asJsonObject.getAsJsonPrimitive("identifier").asLong == 100L }?.asJsonObject
        val weight200 = jsonResponse.getAsJsonArray("weights")
            .find { it.asJsonObject.getAsJsonPrimitive("identifier").asLong == 200L }?.asJsonObject

        then(weight100?.getAsJsonPrimitive("value")?.asFloat).isEqualTo(180.49f)
        then(weight200?.getAsJsonPrimitive("value")?.asFloat).isEqualTo(190.3f)

    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/measures/weight/get",
        httpMethod = RequestMethod.GET,
        parameters = ["clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a"]
    )
    fun `test get weights for invalid client`() {

        // given
        val uuid = UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")

        mockClientRepository
            .mock(MockClientRepository.FIND_BY_UUID)
            .params(mapOf(Pair("uuid", uuid)))
            .returns { null }

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("CustomerNotFoundException.html")
            .hasErrorMessageTitle("CustomerNotFoundException")
            .hasErrorMessageDetail("Could not find any client with uuid: 79275cc8-ed8a-4f8a-b790-ff66f74d758a.")
            .hasErrorMessageStatus("404")
            .hasErrorMessageCode("2001")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()

    }

}
