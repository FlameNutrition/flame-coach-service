package com.coach.flame.testing.component.api.customer

import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.domain.maker.LoginInfoDtoBuilder
import com.coach.flame.domain.maker.LoginInfoDtoMaker
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.maker.UserBuilder
import com.coach.flame.jpa.entity.maker.UserMaker
import com.coach.flame.testing.assertion.http.ErrorAssert
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
import java.util.*

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
        val salt = saltTool.generate()
        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(
                with(
                    ClientDtoMaker.loginInfo,
                    LoginInfoDtoBuilder.maker()
                        .but(
                            with(LoginInfoDtoMaker.username, "test@gmail.com"),
                            with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                            with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now())
                        ).make()
                )
            )
            .make()
            .toClient()
        val user = UserBuilder.maker()
            .but(
                with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.client, client),
                with(UserMaker.key, salt),
                with(UserMaker.password, hashPasswordTool.generate("12345", salt))
            )
            .make()
        every { userRepositoryMock.findUserByEmail("test@gmail.com") } returns user
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
        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

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
        every { userRepositoryMock.findUserByEmail("test.test@gmail.com") } returns null

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("CustomerUsernameOrPasswordException.html")
            .hasErrorMessageTitle("CustomerUsernameOrPasswordException")
            .hasErrorMessageDetail("Username invalid.")
            .hasErrorMessageStatus("400")
            .hasErrorMessageCode("2003")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/newCustomerSessionInvalidUsername.json",
        endpoint = "/api/customer/newSession",
        httpMethod = RequestMethod.POST
    )
    fun `test get a new client session but password is invalid`() {

        //given
        val salt = saltTool.generate()
        val user = UserBuilder.maker()
            .but(
                with(UserMaker.email, "test.test@gmail.com"),
                with(UserMaker.client, ClientDtoBuilder.makerWithLoginInfo().make().toClient()),
                with(UserMaker.key, salt),
                with(UserMaker.password, hashPasswordTool.generate("other_password", salt))
            )
            .make()
        every { userRepositoryMock.findUserByEmail("test.test@gmail.com") } returns user

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("CustomerUsernameOrPasswordException.html")
            .hasErrorMessageTitle("CustomerUsernameOrPasswordException")
            .hasErrorMessageDetail("Password invalid.")
            .hasErrorMessageStatus("400")
            .hasErrorMessageCode("2003")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()

    }


    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/newCustomerSession.json",
        endpoint = "/api/customer/newSession",
        httpMethod = RequestMethod.POST
    )
    fun `test get a new client session but throws unexpected exception`() {

        // given
        val salt = saltTool.generate()
        val user = UserBuilder.maker()
            .but(
                with(UserMaker.client, ClientDtoBuilder.makerWithLoginInfo().make().toClient()),
                with(UserMaker.key, salt),
                with(UserMaker.password, hashPasswordTool.generate("12345", salt))
            )
            .make()
        every { userRepositoryMock.findUserByEmail("test@gmail.com") } returns user
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

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("InternalServerException.html")
            .hasErrorMessageTitle("InternalServerException")
            .hasErrorMessageDetail("This is an internal problem, please contact the admin system.")
            .hasErrorMessageStatus("500")
            .hasErrorMessageCode("9999")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()
    }


}
