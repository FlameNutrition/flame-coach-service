package com.coach.flame.testing.component.client

import com.coach.flame.jpa.entity.ClientMaker
import com.coach.flame.jpa.entity.UserMaker
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.ClientTypeRepository
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
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod
import java.time.LocalDateTime

class AuthClientTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var clientTypeRepositoryMock: ClientTypeRepository

    @Autowired
    private lateinit var clientRepositoryMock: ClientRepository

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/registerNewClient.json",
        endpoint = "/api/client/create",
        httpMethod = RequestMethod.POST
    )
    fun `register new client`() {

        // given
        val user = userMaker.but(
            with(UserMaker.email, "test@gmail.com"),
            with(UserMaker.password, "test")
        ).make()
        val client = clientMaker.but(
            with(ClientMaker.firstname, "Nuno"),
            with(ClientMaker.lastname, "Bento"),
            with(ClientMaker.user, user),
        ).make()
        every { clientTypeRepositoryMock.getByType("CLIENT") } returns clientTypeMaker.make()
        every { clientRepositoryMock.saveAndFlush(any()) } returns client

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

        val expiration = LocalDateTime.parse(jsonResponse.getAsJsonPrimitive("expiration").asString)

        then(expiration).isAfter(LocalDateTime.now().plusHours(1))

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/registerNewClientMissingParam.json",
        endpoint = "/api/client/create",
        httpMethod = RequestMethod.POST
    )
    fun `register new client with missing mandatory param`() {

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
        pathOfRequest = "requests/component/client/registerNewClient.json",
        endpoint = "/api/client/create",
        httpMethod = RequestMethod.POST
    )
    fun `register new client duplicated`() {

        every { clientTypeRepositoryMock.getByType("CLIENT") } returns clientTypeMaker.make()
        every { clientRepositoryMock.saveAndFlush(any()) } throws DataIntegrityViolationException("SQL Error -> Duplicate client")

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

        thenErrorMessageType(body).endsWith("ClientRegisterDuplicateException.html")
        thenErrorMessageTitle(body).isEqualTo("ClientRegisterDuplicateException")
        thenErrorMessageDetail(body).isEqualTo("The following client already exists")
        thenErrorMessageStatus(body).isEqualTo("400")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/registerNewClient.json",
        endpoint = "/api/client/create",
        httpMethod = RequestMethod.POST
    )
    fun `register new client but occorred an internal exception`() {

        every { clientTypeRepositoryMock.getByType("CLIENT") } returns clientTypeMaker.make()
        every { clientRepositoryMock.saveAndFlush(any()) } throws Exception("Ops...something is wrong!")

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

        thenErrorMessageType(body).endsWith("ClientRegisterException.html")
        thenErrorMessageTitle(body).isEqualTo("ClientRegisterException")
        thenErrorMessageDetail(body).isEqualTo("Problem occurred when try to register a new client")
        thenErrorMessageStatus(body).isEqualTo("500")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()
    }

}