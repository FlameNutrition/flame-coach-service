package com.coach.flame.testing.component.api.appointments

import com.coach.flame.date.DateHelper
import com.coach.flame.testing.assertion.http.AppointmentAssert
import com.coach.flame.testing.assertion.http.ErrorAssert
import com.coach.flame.testing.component.base.BaseComponentTest
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
import java.util.*

class GetNextCoachAppointmentTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/coach/getNext",
        httpMethod = RequestMethod.GET,
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369ae"
        ]
    )
    fun `test get next coach appointment`() {

        val clientIdentifier = UUID.fromString("0f1c2e7f-a6c8-4f0d-8edc-01c7a5014419")
        val coachIdentifier = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")

        val appointment1UUID = UUID.fromString("2fbf61a6-3b72-4313-b858-43dbf81198dc")
        val appointment2UUID = UUID.fromString("6633e99b-4e1f-48e3-b4b0-f341292fe608")

        // given
        val client = oneClientAvailable(clientIdentifier)
        val coach = oneCoach(coachIdentifier, mutableListOf())
        val appointments = AppointmentsDataGenerator.Builder(coach, client)
            .addIdentifierWithInterval(
                appointment1UUID,
                DateHelper.toZonedDateTime("2021-07-14T10:52:52+01:00"),
                DateHelper.toZonedDateTime("2021-07-14T11:52:52+01:00")
            )
            .addIdentifierWithInterval(
                appointment2UUID,
                DateHelper.toZonedDateTime("2021-08-14T10:52:52+01:00"),
                DateHelper.toZonedDateTime("2021-08-14T11:52:52+01:00")
            )
            .build()

        mockAppointmentsRepository.mockGetAppointmentByCoachAndDttmStarts(coach, appointments)

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

        AppointmentAssert.assertThat(jsonResponse).hasSize(1)
            .hasIdentifier(appointment1UUID)

        AppointmentAssert.assertThat(jsonResponse)
            .hasDttmStarts(appointment1UUID, "2021-07-14T10:52:52+01:00")
            .hasDttmEnds(appointment1UUID, "2021-07-14T11:52:52+01:00")
            .hasPrice(appointment1UUID, 10.5f)
            .hasNotes(appointment1UUID, "Appointment")
            .hasClient(appointment1UUID, client.uuid)
            .hasClientFirstname(appointment1UUID, client.firstName)
            .hasClientLastname(appointment1UUID, client.lastName)
    }

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/coach/getNext",
        httpMethod = RequestMethod.GET,
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369ae"
        ]
    )
    fun `test get next coach appointment but not found appointments`() {

        val coachIdentifier = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")

        val client = oneClientAvailable(coachIdentifier)
        val coach = oneCoach(coachIdentifier, mutableListOf(client))
        mockAppointmentsRepository.mockGetAppointmentByCoachAndDttmStarts(coach, listOf())

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
            .hasErrorMessageTypeEndsWith("AppointmentNotFoundException.html")
            .hasErrorMessageTitle("AppointmentNotFoundException")
            .hasErrorMessageDetail("Appointment not found.")
            .hasErrorMessageStatus("404")
            .hasErrorMessageCode("2101")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()

    }

}
