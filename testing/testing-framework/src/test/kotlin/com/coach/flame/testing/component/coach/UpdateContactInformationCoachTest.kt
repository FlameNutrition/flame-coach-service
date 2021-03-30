package com.coach.flame.testing.component.coach

import com.coach.flame.jpa.entity.*
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.every
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

class UpdateContactInformationCoachTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/coach/updateContactInfoCoach.json",
        endpoint = "/api/coach/updateContactInformation",
        httpMethod = RequestMethod.POST,
        headers = ["coachIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test update contact information for coach`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuid),
                with(CoachMaker.firstname, "Nuno"),
                with(CoachMaker.lastname, "Bento"),
                with(CoachMaker.phoneCode, "+44"),
                with(CoachMaker.phoneNumber, "2244556677"),
                with(CoachMaker.country, CountryBuilder.maker()
                    .but(with(CountryMaker.countryCode, "PT"),
                        with(CountryMaker.externalValue, "Portugal"))
                    .make()))
            .make()
        val coachCapture = slot<Coach>()

        every { coachRepositoryMock.findByUuid(uuid) } returns coach
        every { countryConfigCacheMock.getValue("BR") } returns Optional.of(CountryBuilder.maker()
            .but(with(CountryMaker.countryCode, "BR"),
                with(CountryMaker.externalValue, "Brazil"))
            .make())
        every { coachRepositoryMock.save(capture(coachCapture)) } answers { coachCapture.captured }

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

        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("firstName").asString).isEqualTo("Nuno")
        then(jsonResponse.getAsJsonPrimitive("lastName").asString).isEqualTo("Bento")
        then(jsonResponse.getAsJsonPrimitive("phoneCode").asString).isEqualTo("+44")
        then(jsonResponse.getAsJsonPrimitive("phoneNumber").asString).isEqualTo("2244556677")
        then(jsonResponse.getAsJsonObject("country").get("code").asString).isEqualTo("BR")
        then(jsonResponse.getAsJsonObject("country").get("value").asString).isEqualTo("Brazil")

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/coach/updateContactInfoCoach.json",
        endpoint = "/api/coach/updateContactInformation",
        httpMethod = RequestMethod.POST,
        headers = ["coachIdentifier:e59343bc-"]
    )
    fun `test update contact information for coach using invalid uuid`() {

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("InternalServerException.html")
        thenErrorMessageTitle(body).isEqualTo("InternalServerException")
        thenErrorMessageDetail(body).isEqualTo("This is an internal problem, please contact the admin system")
        thenErrorMessageStatus(body).isEqualTo("500")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/coach/updateContactInfoWithNullValuesCoach.json",
        endpoint = "/api/coach/updateContactInformation",
        httpMethod = RequestMethod.POST,
        headers = ["coachIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test update contact information for coach without unmandatory param request`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuid),
                with(CoachMaker.firstname, "Nuno"),
                with(CoachMaker.lastname, "Neves"),
                with(CoachMaker.phoneCode, "+44"),
                with(CoachMaker.phoneNumber, "1122345"),
                with(CoachMaker.country, CountryBuilder.maker()
                    .but(with(CountryMaker.countryCode, "PT"),
                        with(CountryMaker.externalValue, "Portugal"))
                    .make()))
            .make()
        val coachCapture = slot<Coach>()

        every { coachRepositoryMock.findByUuid(uuid) } returns coach
        every { coachRepositoryMock.save(capture(coachCapture)) } answers { coachCapture.captured }

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

        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("firstName").asString).isEqualTo("Nuno")
        then(jsonResponse.getAsJsonPrimitive("lastName").asString).isEqualTo("Bento")
        then(jsonResponse.get("phoneCode").isJsonNull).isTrue
        then(jsonResponse.get("phoneNumber").isJsonNull).isTrue
        then(jsonResponse.get("country").isJsonNull).isTrue

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/coach/updateContactInfoWithInvalidCountryCoach.json",
        endpoint = "/api/coach/updateContactInformation",
        httpMethod = RequestMethod.POST,
        headers = ["coachIdentifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test update contact information for coach with invalid country`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuid),
                with(CoachMaker.firstname, "Nuno"),
                with(CoachMaker.lastname, "Bento"),
                with(CoachMaker.phoneCode, "+44"),
                with(CoachMaker.phoneNumber, "2244556677"),
                with(CoachMaker.country, CountryBuilder.maker()
                    .but(with(CountryMaker.countryCode, "PT"),
                        with(CountryMaker.externalValue, "Portugal"))
                    .make()))
            .make()
        val coachCapture = slot<Coach>()

        every { coachRepositoryMock.findByUuid(uuid) } returns coach
        every { countryConfigCacheMock.getValue("KILL") } returns Optional.empty()
        every { coachRepositoryMock.save(capture(coachCapture)) } answers { coachCapture.captured }

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("UnexpectedConfigException.html")
        thenErrorMessageTitle(body).isEqualTo("UnexpectedConfigException")
        thenErrorMessageDetail(body).isEqualTo("Country Code: 'KILL' is not present in the system.")
        thenErrorMessageStatus(body).isEqualTo("500")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }


}
