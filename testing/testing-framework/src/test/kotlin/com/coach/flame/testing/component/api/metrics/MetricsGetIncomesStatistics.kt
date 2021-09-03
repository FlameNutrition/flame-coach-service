package com.coach.flame.testing.component.api.metrics

import com.coach.flame.domain.IncomeDto
import com.coach.flame.domain.maker.IncomeDtoBuilder
import com.coach.flame.domain.maker.IncomeDtoMaker
import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.ClientStatus
import com.coach.flame.jpa.entity.Income.Companion.toIncome
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
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
import java.time.LocalDate
import java.util.*

class MetricsGetIncomesStatistics : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/metrics/incomes",
        httpMethod = RequestMethod.GET,
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "from:2021-05-30",
            "to:2021-06-30"
        ]
    )
    fun `test get incomes metrics from coach`() {

        // given
        val incomePending1 = IncomeDtoBuilder.maker()
            .but(with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.PENDING))
            .make()
        val incomePending2 = IncomeDtoBuilder.maker()
            .but(with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.PENDING))
            .make()
        val incomeAccepted = IncomeDtoBuilder.maker()
            .but(with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.ACCEPTED))
            .make()
        val incomeRejected = IncomeDtoBuilder.maker()
            .but(with(IncomeDtoMaker.status, IncomeDto.IncomeStatus.REJECTED))
            .make()

        val listOfIncomes = listOf(incomePending1, incomePending2, incomeAccepted, incomeRejected)

        every {
            coachOperationsMock.getIncome(
                UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae"),
                LocalDate.of(2021, 5, 30),
                LocalDate.of(2021, 6, 30)
            )
        } returns listOfIncomes.map { it.toIncome() }

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

        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo("3c5845f1-4a90-4396-8610-7261761369ae")

        then(jsonResponse.getAsJsonObject("incomesStatus").get("accepted").asInt).isEqualTo(1)
        then(jsonResponse.getAsJsonObject("incomesStatus").get("pending").asInt).isEqualTo(2)
        then(jsonResponse.getAsJsonObject("incomesStatus").get("rejected").asInt).isEqualTo(1)

    }

    @Test
    @LoadRequest(
        endpoint = "/api/metrics/incomes",
        httpMethod = RequestMethod.GET,
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369a7",
            "from:2021/05/30",
            "to:2021-06-30"
        ]
    )
    fun `test get incomes metrics from coach but received invalid dates`() {

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("RestInvalidRequestException.html")
        thenErrorMessageTitle(body).isEqualTo("RestInvalidRequestException")
        thenErrorMessageDetail(body).contains("Invalid date format. Date: 2021/05/30")
        thenErrorMessageStatus(body).isEqualTo("400")
        thenErrorCode(body).isEqualTo("1001")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

}
