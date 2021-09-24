package com.coach.flame.testing.component.api.client.measures

import com.coach.flame.domain.maker.MeasureDtoBuilder
import com.coach.flame.domain.maker.MeasureDtoMaker
import com.coach.flame.jpa.entity.ClientMeasureWeight.Companion.toClientMeasureWeight
import com.coach.flame.jpa.entity.MeasureConfig
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
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

class EditWeightClientTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/measures/editWeightMeasure.json",
        endpoint = "/api/client/measures/weight/edit",
        httpMethod = RequestMethod.POST,
        parameters = [
            "identifier:100",
            "clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a"
        ]
    )
    fun `test edit weight for client`() {

        // given
        val uuid = UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")

        val weight0 = MeasureDtoBuilder.makerWithId().make()
        val weight1 = MeasureDtoBuilder.maker()
            .but(
                with(MeasureDtoMaker.id, 100L),
                with(MeasureDtoMaker.value, 70.5f)
            )
            .make()
        val client = ClientBuilder.maker()
            .but(
                with(
                    ClientMaker.clientMeasureWeight, mutableListOf(
                        weight0.toClientMeasureWeight(),
                        weight1.toClientMeasureWeight()
                    )
                )
            )
            .make()

        mockClientRepository
            .mock(MockClientRepository.FIND_BY_UUID)
            .params(mapOf(Pair("uuid", uuid)))
            .returns { client }

        mockClientRepository
            .mock(MockClientRepository.SAVE_AND_FLUSH)
            .capture()

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

        val weight = jsonResponse.getAsJsonArray("weights")
            .find { it.asJsonObject.getAsJsonPrimitive("identifier").asLong == 100L }?.asJsonObject

        then(weight?.getAsJsonPrimitive("date")?.asString).isEqualTo("2020-02-20")
        then(weight?.getAsJsonPrimitive("value")?.asFloat).isEqualTo(100.5f)

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/measures/editWeightMeasureLbs.json",
        endpoint = "/api/client/measures/weight/edit",
        httpMethod = RequestMethod.POST,
        parameters = [
            "identifier:100",
            "clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a"
        ]
    )
    fun `test edit weights from client with measure type lbs`() {

        // given
        val uuid = UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")

        val weight0 = MeasureDtoBuilder.maker()
            .but(
                with(MeasureDtoMaker.id, 100L),
                with(MeasureDtoMaker.value, 90.9f)
            )
            .make()
        val weight1 = MeasureDtoBuilder.maker()
            .but(
                with(MeasureDtoMaker.id, 200L),
                with(MeasureDtoMaker.value, 86.32f)
            )
            .make()
        val client = ClientBuilder.maker()
            .but(
                with(
                    ClientMaker.clientMeasureWeight,
                    mutableListOf(weight0.toClientMeasureWeight(), weight1.toClientMeasureWeight())
                ),
                with(ClientMaker.measureConfig, MeasureConfig.LBS_IN)
            )
            .make()

        mockClientRepository
            .mock(MockClientRepository.FIND_BY_UUID)
            .params(mapOf(Pair("uuid", uuid)))
            .returns { client }

        val clientCaptured = mockClientRepository
            .mock(MockClientRepository.SAVE_AND_FLUSH)
            .capture()

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andExpect { }
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

        then(weight100?.getAsJsonPrimitive("value")?.asFloat).isEqualTo(200.4f)
        then(weight200?.getAsJsonPrimitive("value")?.asFloat).isEqualTo(190.3f)

        val weight100databaseEntity = clientCaptured.captured.clientMeasureWeight.find { it.toDto().id == 100L }

        // Weight should be saved in kgs
        then(weight100databaseEntity?.weight).isEqualTo(90.9f)

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/measures/editWeightMeasure.json",
        endpoint = "/api/client/measures/weight/edit",
        httpMethod = RequestMethod.POST,
        parameters = [
            "clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a",
            "identifier:200",
        ]

    )
    fun `test add weight for invalid measure identifier`() {

        // given
        val uuid = UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")

        val weight0 = MeasureDtoBuilder.maker()
            .but(
                with(MeasureDtoMaker.id, 100L),
                with(MeasureDtoMaker.value, 70.5f)
            )
            .make()
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.clientMeasureWeight, mutableListOf(weight0.toClientMeasureWeight())))
            .make()

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
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("MeasureNotFoundException.html")
            .hasErrorMessageTitle("MeasureNotFoundException")
            .hasErrorMessageDetail("Measure is not present in the list.")
            .hasErrorMessageStatus("404")
            .hasErrorMessageCode("6001")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()

    }

}
