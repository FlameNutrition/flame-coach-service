package com.coach.flame.testing.component.api.client.measures

import com.coach.flame.domain.maker.MeasureDtoBuilder
import com.coach.flame.domain.maker.MeasureDtoMaker
import com.coach.flame.jpa.entity.ClientMeasureWeight.Companion.toClientMeasureWeight
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
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

class DeleteWeightClientTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/client/measures/weight/delete",
        httpMethod = RequestMethod.DELETE,
        parameters = [
            "identifier:100",
            "clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a"
        ]
    )
    fun `test delete weight for client`() {

        // given
        val uuid = UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")

        val weight0 = MeasureDtoBuilder.makerWithId().make()
        val weight1 = MeasureDtoBuilder.maker()
            .but(with(MeasureDtoMaker.id, 100L),
                with(MeasureDtoMaker.value, 70.5f))
            .make()
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.clientMeasureWeight, mutableListOf(
                weight0.toClientMeasureWeight(),
                weight1.toClientMeasureWeight())))
            .make()

        mockClientRepository.findByUuid(uuid, client)
        mockClientRepository.saveAndFlush()

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

        then(jsonResponse.getAsJsonArray("weights")).hasSize(1)

        val weight = jsonResponse.getAsJsonArray("weights")
            .find { it.asJsonObject.getAsJsonPrimitive("identifier").asLong == 100L }

        then(weight).isNull()

    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/measures/weight/delete",
        httpMethod = RequestMethod.DELETE,
        parameters = [
            "clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a",
            "identifier:200",
        ]

    )
    fun `test delete weight for invalid measure identifier`() {

        // given
        val uuid = UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")

        val weight0 = MeasureDtoBuilder.maker()
            .but(with(MeasureDtoMaker.id, 100L),
                with(MeasureDtoMaker.value, 70.5f))
            .make()
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.clientMeasureWeight, mutableListOf(weight0.toClientMeasureWeight())))
            .make()

        mockClientRepository.findByUuid(uuid, client)

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

        thenErrorMessageType(body).endsWith("MeasureNotFoundException.html")
        thenErrorMessageTitle(body).isEqualTo("MeasureNotFoundException")
        thenErrorMessageDetail(body).isEqualTo("Measure is not present in the list")
        thenErrorMessageStatus(body).isEqualTo("404")
        thenErrorCode(body).isEqualTo("6001")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

}
