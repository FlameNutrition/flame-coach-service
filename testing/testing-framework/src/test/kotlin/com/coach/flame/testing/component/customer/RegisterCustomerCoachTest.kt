package com.coach.flame.testing.component.customer

import com.coach.flame.jpa.entity.ClientTypeMaker
import com.coach.flame.jpa.entity.CoachMaker
import com.coach.flame.jpa.entity.UserMaker
import com.coach.flame.jpa.entity.UserSessionMaker
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.every
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod
import java.time.LocalDateTime

class RegisterCustomerCoachTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/registerNewCustomerCoach.json",
        endpoint = "/api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `test register new coach`() {

        // given
        val expirationDate = LocalDateTime.now()
        val user = userMaker
            .but(with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.password, "test"),
                with(UserMaker.userSession, userSessionMaker
                    .but(with(UserSessionMaker.expirationDate, expirationDate.plusHours(2)))))
            .make()
        val clientType = clientTypeMaker
            .but(with(ClientTypeMaker.type, "COACH"))
            .make()
        val coach = coachMaker
            .but(
                with(CoachMaker.clientType, clientType),
                with(CoachMaker.firstname, "Nuno"),
                with(CoachMaker.lastname, "Bento"),
                with(CoachMaker.user, user))
            .make()
        every { clientTypeRepositoryMock.getByType("COACH") } returns clientType
        every { coachRepositoryMock.save(any()) } returns coach

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        val jsonResponse = JsonBuilder.getJsonFromString(mvnResponse.response.contentAsString)

        then(jsonResponse.getAsJsonPrimitive("username").asString).isEqualTo("test@gmail.com")
        then(jsonResponse.getAsJsonPrimitive("firstname").asString).isEqualTo("Nuno")
        then(jsonResponse.getAsJsonPrimitive("lastname").asString).isEqualTo("Bento")
        then(jsonResponse.getAsJsonPrimitive("token").asString).isNotNull
        then(jsonResponse.getAsJsonPrimitive("type").asString).isEqualTo("COACH")
        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(coach.uuid.toString())

        val expiration = LocalDateTime.parse(jsonResponse.getAsJsonPrimitive("expiration").asString)

        then(expiration).isEqualTo(expirationDate.plusHours(2))

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/registerNewCustomerMissingParam.json",
        endpoint = "/api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `test register new client with missing mandatory param`() {

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromString(mvnResponse.response.contentAsString)

        thenErrorMessageType(body).endsWith("RestInvalidRequestException.html")
        thenErrorMessageTitle(body).isEqualTo("RestInvalidRequestException")
        thenErrorMessageDetail(body).isEqualTo("java.lang.IllegalArgumentException: Missing required parameter request: lastname")
        thenErrorMessageStatus(body).isEqualTo("400")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/registerNewCustomerCoach.json",
        endpoint = "/api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `test register new client duplicated`() {

        every { clientTypeRepositoryMock.getByType("COACH") } returns clientTypeMaker
            .but(with(ClientTypeMaker.type, "COACH"))
            .make()
        every { coachRepositoryMock.save(any()) } throws DataIntegrityViolationException("SQL Error -> Duplicate client")

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromString(mvnResponse.response.contentAsString)

        thenErrorMessageType(body).endsWith("CustomerRegisterDuplicateException.html")
        thenErrorMessageTitle(body).isEqualTo("CustomerRegisterDuplicateException")
        thenErrorMessageDetail(body).isEqualTo("The following customer already exists")
        thenErrorMessageStatus(body).isEqualTo("400")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/registerNewCustomerCoach.json",
        endpoint = "/api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `test register new client but occurred an internal exception`() {

        every { clientTypeRepositoryMock.getByType("COACH") } returns clientTypeMaker
            .but(with(ClientTypeMaker.type, "COACH"))
            .make()
        every { coachRepositoryMock.save(any()) } throws Exception("Ops...something is wrong!")

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromString(mvnResponse.response.contentAsString)

        thenErrorMessageType(body).endsWith("InternalServerException.html")
        thenErrorMessageTitle(body).isEqualTo("InternalServerException")
        thenErrorMessageDetail(body).isEqualTo("This is an internal problem, please contact the admin system")
        thenErrorMessageStatus(body).isEqualTo("500")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()
    }

}