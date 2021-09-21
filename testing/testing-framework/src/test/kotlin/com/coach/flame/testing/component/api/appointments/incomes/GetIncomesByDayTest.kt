package com.coach.flame.testing.component.api.appointments.incomes

import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.component.base.utils.AppointmentsHelper.multipleAppointments
import com.coach.flame.testing.component.base.utils.ClientHelper.oneClientPending
import com.coach.flame.testing.component.base.utils.CoachHelper.oneCoach
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
import java.time.LocalDate
import java.util.*

class GetIncomesByDayTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/income/coach/getAcceptedIncomes",
        httpMethod = RequestMethod.GET,
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "from:2020-02-01",
            "to:2020-02-20",
            "aggregateType:DAY"
        ]
    )
    fun `test get incomes aggregated by day`() {

        val from = LocalDate.of(2020, 2, 1).atStartOfDay()
        val to = LocalDate.of(2020, 2, 20).atStartOfDay()

        val clientIdentifier = UUID.fromString("1bc5168f-6cb6-4a64-a676-afa07aa1d733")
        val coachIdentifier = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")

        val listOfAppointmentsIdentifier = listOf(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        )

        val incomesFrom = listOf(
            "2020-01-01T10:52:52+01:00",
            "2020-02-03T10:52:52+01:00",
            "2020-02-10T10:52:52+01:00",
            "2020-05-10T10:52:52+01:00",
            "2020-07-12T10:52:52+01:00",
            "2021-02-01T10:52:52+01:00",
            "2021-05-10T10:52:52+01:00",
            "2022-01-01T10:52:52+01:00",
        )

        val incomesTo = listOf(
            "2020-01-01T11:52:52+01:00",
            "2020-02-03T11:52:52+01:00",
            "2020-02-10T11:52:52+01:00",
            "2020-05-10T11:52:52+01:00",
            "2020-07-12T11:52:52+01:00",
            "2021-02-01T11:52:52+01:00",
            "2021-05-10T11:52:52+01:00",
            "2022-01-01T11:52:52+01:00",
        )

        // given
        val client = oneClientPending(clientIdentifier)
        val coach = oneCoach(coachIdentifier, mutableListOf(client))
        val appointments = multipleAppointments(coach, client, listOfAppointmentsIdentifier, incomesFrom, incomesTo)

        mockAppointmentsRepository.mockFindAppointmentsByCoachBetweenDate(coach, from, to, appointments)

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

        then(jsonResponse.getAsJsonObject("incomes").getAsJsonArray("2020-02-03")).hasSize(1)
        then(jsonResponse.getAsJsonObject("incomes").getAsJsonArray("2020-02-10")).hasSize(1)
    }
}
