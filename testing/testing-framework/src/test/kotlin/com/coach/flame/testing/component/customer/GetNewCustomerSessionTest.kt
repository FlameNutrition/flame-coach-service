package com.coach.flame.testing.component.customer

import com.coach.flame.jpa.entity.*
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod
import java.sql.SQLException
import java.time.LocalDateTime

class GetNewCustomerSessionTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/newCustomerSession.json",
        endpoint = "/api/customer/newSession",
        httpMethod = RequestMethod.POST
    )
    fun `test get a new client session`() {

        // given
        val oldDate = LocalDateTime.now()
        val client = clientMaker
            .but(with(ClientMaker.userSession, userSessionMaker
                .but(with(UserSessionMaker.expirationDate, oldDate))
                .make()))
            .but(with(ClientMaker.user, userMaker
                .but(with(UserMaker.email, "test@gmail.com"))
                .make()))
            .make()
        val user = userMaker
            .but(with(UserMaker.email, "test@gmail.com"))
            .but(with(UserMaker.password, "12345"))
            .but(with(UserMaker.client, client))
            .make()
        every { userRepositoryMock.findUserByEmailAndPassword("test@gmail.com", "12345") } returns user
        every { userSessionRepositoryMock.save(any()) } returns mockk()

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
        then(jsonResponse.getAsJsonPrimitive("firstname").asString).isNotEmpty
        then(jsonResponse.getAsJsonPrimitive("lastname").asString).isNotEmpty
        then(jsonResponse.getAsJsonPrimitive("token").asString).isNotNull
        then(jsonResponse.getAsJsonPrimitive("type").asString).isEqualTo("CLIENT")
        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(client.uuid.toString())

        val expiration = LocalDateTime.parse(jsonResponse.getAsJsonPrimitive("expiration").asString)
        then(oldDate).isNotEqualTo(expiration)

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/newCustomerSessionInvalidUsername.json",
        endpoint = "/api/customer/newSession",
        httpMethod = RequestMethod.POST
    )
    fun `test get a new client session but username is invalid`() {

        // given
        every { userRepositoryMock.findUserByEmailAndPassword("test.test@gmail.com", "12345") } returns null

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

        thenErrorMessageType(body).endsWith("CustomerUsernameOrPasswordException.html")
        thenErrorMessageTitle(body).isEqualTo("CustomerUsernameOrPasswordException")
        thenErrorMessageDetail(body).isEqualTo("Username or password invalid")
        thenErrorMessageStatus(body).isEqualTo("400")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/newCustomerSession.json",
        endpoint = "/api/customer/newSession",
        httpMethod = RequestMethod.POST
    )
    fun `test get a new client session but throws unexpected exception`() {

        // given
        val user = UserBuilder.maker()
            .but(with(UserMaker.client, ClientBuilder.default()))
            .make()
        every { userRepositoryMock.findUserByEmailAndPassword("test@gmail.com", "12345") } returns user
        every { userSessionRepositoryMock.save(any()) } throws SQLException("This is a sensible information")

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