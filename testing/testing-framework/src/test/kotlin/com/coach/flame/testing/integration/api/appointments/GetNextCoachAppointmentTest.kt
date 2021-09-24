package com.coach.flame.testing.integration.api.appointments

import com.coach.flame.testing.assertion.http.ErrorAssert
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.coach.flame.testing.integration.base.BaseIntegrationTest
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod

class GetNextCoachAppointmentTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @BeforeEach
    override fun setup() {
        super.setup()
    }

    @Test
    @LoadRequest(
        endpoint = "/api/appointment/coach/getNext",
        httpMethod = RequestMethod.GET,
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369ae"
        ]
    )
    fun `test get next coach appointment when appointments are empty`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)

        ErrorAssert.assertThat(JsonBuilder.getJsonFromString(response.body!!))
            .hasErrorMessageTypeEndsWith("AppointmentNotFoundException.html")
            .hasErrorMessageTitle("AppointmentNotFoundException")
            .hasErrorMessageDetail("Appointment not found.")
            .hasErrorMessageStatus("404")
            .hasErrorMessageCode("2101")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()
    }

}
