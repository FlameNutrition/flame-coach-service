package com.coach.flame.testing.component.api.appointments

import com.coach.flame.customer.CustomerNotFoundException
import com.coach.flame.testing.assertion.http.AppointmentAssert
import com.coach.flame.testing.assertion.http.ErrorAssert
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.component.base.mock.MockAppointmentsRepository
import com.coach.flame.testing.component.base.mock.MockClientRepository
import com.coach.flame.testing.component.base.utils.AppointmentsHelper.twoAppointments
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
import java.util.*

class GetClientAppointmentsTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/client/get",
        httpMethod = RequestMethod.GET,
        parameters = [
            "clientIdentifier:0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419"
        ]
    )
    fun `test get appointments for specific client`() {

        val coachIdentifier = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val clientIdentifier = UUID.fromString("0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419")

        val appointment1UUID = UUID.fromString("2fbf61a6-3b72-4313-b858-43dbf81198dc")
        val appointment2UUID = UUID.fromString("6633e99b-4e1f-48e3-b4b0-f341292fe608")

        // given
        val client = oneClientAvailable(clientIdentifier)
        val coach = oneCoach(coachIdentifier, mutableListOf(client))
        val twoAppointments = twoAppointments(coach, client, listOf(appointment1UUID, appointment2UUID))

        mockClientRepository
            .mock(MockClientRepository.GET_CLIENT)
            .params(mapOf(Pair("uuid", clientIdentifier)))
            .returns { client }

        mockAppointmentsRepository
            .mock(MockAppointmentsRepository.GET_APPOINTMENTS_BY_CLIENT)
            .params(mapOf(Pair("client", client)))
            .returnsMulti { twoAppointments }

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

        AppointmentAssert.assertThat(jsonResponse).hasSize(2)
            .hasIdentifier(appointment1UUID)
            .hasIdentifier(appointment2UUID)

        AppointmentAssert.assertThat(jsonResponse)
            .hasDttmStarts(appointment1UUID, "2021-07-14T10:52:52+01:00")
            .hasDttmEnds(appointment1UUID, "2021-07-14T11:52:52+01:00")
            .hasPrice(appointment1UUID, 100.5f)
            .hasNotes(appointment1UUID, "First appointment")
            .hasClient(appointment1UUID, client.uuid)
            .hasClientFirstname(appointment1UUID, client.firstName)
            .hasClientLastname(appointment1UUID, client.lastName)

        AppointmentAssert.assertThat(jsonResponse)
            .hasDttmStarts(appointment2UUID, "2021-07-20T10:52:52+01:00")
            .hasDttmEnds(appointment2UUID, "2021-07-20T11:52:52+01:00")
            .hasPrice(appointment2UUID, 200.5f)
            .hasNotes(appointment2UUID, "Review appointment")
            .hasClient(appointment2UUID, client.uuid)
            .hasClientFirstname(appointment2UUID, client.firstName)
            .hasClientLastname(appointment2UUID, client.lastName)

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
