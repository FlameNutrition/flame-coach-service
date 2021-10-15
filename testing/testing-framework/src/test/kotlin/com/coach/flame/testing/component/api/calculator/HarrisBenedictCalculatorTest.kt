package com.coach.flame.testing.component.api.calculator

import com.coach.flame.testing.assertion.http.CalculatorAssert
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod

class HarrisBenedictCalculatorTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/calculator/harrisBenedict",
        httpMethod = RequestMethod.GET,
        parameters = [
            "weight:75",
            "height:173",
            "sex:Male",
            "age:31",
            "unit:kg/cm",
            "pal:1"
        ]
    )
    fun `test calculate calories using Harris Benedict calculator`() {

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

        CalculatorAssert.assertThat(jsonResponse)
            .hasResult(2311.72)
            .hasUnit("KILOCALORIES")
            .hasPerUnit("day")

    }

    @Test
    @LoadRequest(
        endpoint = "/api/calculator/harrisBenedict",
        httpMethod = RequestMethod.GET,
        parameters = [
            "weight:75",
            "height:173",
            "sex:Male",
            "age:31",
            "unit:kg/cm",
            "caloriesUnit:joules",
            "pal:1"
        ]
    )
    fun `test calculate calories using Harris Benedict calculator and result in joules`() {

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

        CalculatorAssert.assertThat(jsonResponse)
            .hasResult(9672236.479999999)
            .hasUnit("JOULES")
            .hasPerUnit("day")

    }

}
