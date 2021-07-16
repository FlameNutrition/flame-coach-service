package com.coach.flame.testing.component.api.appointments

import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.component.base.utils.AppointmentsHelper.oneAppointment
import com.coach.flame.testing.component.base.utils.AppointmentsHelper.twoAppointments
import com.coach.flame.testing.component.base.utils.ClientHelper.oneClientAvailable
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
import java.time.LocalDateTime
import java.util.*

class CreateAppointmentTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/create",
        httpMethod = RequestMethod.POST,
        pathOfRequest = "requests/component/appointments/createAppointment.json",
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "clientIdentifier:0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419"
        ]
    )
    fun `test create appointments`() {

        val coachIdentifier = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val client1Identifier = UUID.fromString("1bc5168f-6cb6-4a64-a676-afa07aa1d733")
        val client2Identifier = UUID.fromString("0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419")

        // given
        val client1 = oneClientPending(client1Identifier)
        val client2 = oneClientAvailable(client2Identifier)
        val coach = oneCoach(coachIdentifier, mutableListOf(client1, client2))

        mockCoachRepository.mockFindByUuid(coachIdentifier, coach)
        val capturedAppointment = mockAppointmentsRepository.captureSave()

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

        then(capturedAppointment.isCaptured).isTrue
        then(capturedAppointment.captured.currency).isEqualTo("GBP")
        then(capturedAppointment.captured.price).isEqualTo(156.5f)
        then(capturedAppointment.captured.notes).isEqualTo("This is my first appointment")
        then(capturedAppointment.captured.delete).isFalse
        then(capturedAppointment.captured.coach).isNotNull
        then(capturedAppointment.captured.client).isNotNull
        then(capturedAppointment.captured.dttm).isEqualTo(LocalDateTime.parse("2021-07-14T03:52:52"))

        then(jsonResponse.getAsJsonArray("appointments")).hasSize(1)

        val appointment1 = jsonResponse.getAsJsonArray("appointments").first()

        then(appointment1.asJsonObject.getAsJsonPrimitive("date").asString).isEqualTo("2021-07-14T10:52:52+08:00")
        then(appointment1.asJsonObject.getAsJsonPrimitive("price").asFloat).isEqualTo(156.5f)
        then(appointment1.asJsonObject.getAsJsonPrimitive("notes").asString).isEqualTo("This is my first appointment")
        then(appointment1.asJsonObject.getAsJsonObject("client").getAsJsonPrimitive("identifier").asString).isEqualTo(client2.uuid.toString())
        then(appointment1.asJsonObject.getAsJsonObject("client").getAsJsonPrimitive("firstName").asString).isEqualTo(client2.firstName)
        then(appointment1.asJsonObject.getAsJsonObject("client").getAsJsonPrimitive("lastName").asString).isEqualTo(client2.lastName)

    }

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/create",
        httpMethod = RequestMethod.POST,
        pathOfRequest = "requests/component/appointments/createAppointment.json",
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "clientIdentifier:0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419"
        ]
    )
    fun `test create appointments but coach request is wrong`() {

        mockCoachRepository.mockFindByUuidThrowsException(UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae"))

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

        thenErrorMessageType(body).endsWith("CustomerNotFoundException.html")
        thenErrorMessageTitle(body).isEqualTo("CustomerNotFoundException")
        thenErrorMessageDetail(body).contains("Could not find any coach with uuid: 3c5845f1-4a90-4396-8610-7261761369ae")
        thenErrorMessageStatus(body).isEqualTo("404")
        thenErrorCode(body).isEqualTo("2001")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/create",
        httpMethod = RequestMethod.POST,
        pathOfRequest = "requests/component/appointments/createAppointment.json",
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "clientIdentifier:0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419"
        ]
    )
    fun `test create appointments but client request is wrong`() {

        val coachIdentifier = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val client1Identifier = UUID.fromString("1bc5168f-6cb6-4a64-a676-afa07aa1d733")

        // given
        val client1 = oneClientPending(client1Identifier)
        val coach = oneCoach(coachIdentifier, mutableListOf(client1))

        mockCoachRepository.mockFindByUuid(coachIdentifier, coach)

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

        thenErrorMessageType(body).endsWith("CustomerNotFoundException.html")
        thenErrorMessageTitle(body).isEqualTo("CustomerNotFoundException")
        thenErrorMessageDetail(body).contains("Could not find any client with uuid: 0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419")
        thenErrorMessageStatus(body).isEqualTo("404")
        thenErrorCode(body).isEqualTo("2001")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

}
