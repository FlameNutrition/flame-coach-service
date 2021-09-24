package com.coach.flame.testing.component.api.appointments

import com.coach.flame.customer.CustomerNotFoundException
import com.coach.flame.date.DateHelper.toZonedDateTime
import com.coach.flame.testing.assertion.http.AppointmentAssert
import com.coach.flame.testing.assertion.http.ErrorAssert
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.component.base.mock.MockAppointmentsRepository
import com.coach.flame.testing.component.base.mock.MockClientRepository
import com.coach.flame.testing.component.base.utils.AppointmentsDataGenerator
import com.coach.flame.testing.component.base.utils.ClientHelper.oneClientAvailable
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

class GetClientAppointmentsWithIntervalFilterTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/client/get",
        httpMethod = RequestMethod.GET,
        parameters = [
            "clientIdentifier:0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419",
            "from:2021-01-12",
            "to:2021-12-10"
        ]
    )
    fun `test get appointments for specific client`() {

        val coachIdentifier = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val clientIdentifier = UUID.fromString("0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419")

        val appointment1UUID = UUID.fromString("2fbf61a6-3b72-4313-b858-43dbf81198dc")

        // given
        val client = oneClientAvailable(clientIdentifier)
        val coach = oneCoach(coachIdentifier, mutableListOf(client))
        val appointments = AppointmentsDataGenerator.Builder(coach, client)
            .addIdentifierWithInterval(
                appointment1UUID,
                toZonedDateTime("2021-07-14T10:52:52+01:00"),
                toZonedDateTime("2021-07-14T11:52:52+01:00")
            )
            .build()

        mockClientRepository
            .mock(MockClientRepository.GET_CLIENT)
            .params(mapOf(Pair("uuid", clientIdentifier)))
            .returns { client }

        mockAppointmentsRepository
            .mock(MockAppointmentsRepository.GET_APPOINTMENTS_BY_CLIENT_BETWEEN_DATES)
            .params(
                mapOf(
                    Pair("client", client),
                    Pair("from", LocalDate.of(2021, 1, 12).atStartOfDay()),
                    Pair("to", LocalDate.of(2021, 12, 10).plusDays(1).atStartOfDay())
                )
            )
            .returnsMulti { appointments }

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

        AppointmentAssert.assertThat(jsonResponse)
            .hasSize(1)
    }

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/client/get",
        httpMethod = RequestMethod.GET,
        parameters = [
            "clientIdentifier:0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419"
        ]
    )
    fun `test get appointments for specific client but client request is wrong`() {

        val clientIdentifier = UUID.fromString("0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419")

        mockClientRepository
            .mock(MockClientRepository.GET_CLIENT)
            .params(mapOf(Pair("uuid", clientIdentifier)))
            .returns { throw CustomerNotFoundException("Could not find any client with uuid: 0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419.") }

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("CustomerNotFoundException.html")
            .hasErrorMessageTitle("CustomerNotFoundException")
            .hasErrorMessageDetail("Could not find any client with uuid: 0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419.")
            .hasErrorMessageStatus("404")
            .hasErrorMessageCode("2001")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()

    }

}
