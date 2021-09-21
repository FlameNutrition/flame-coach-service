package com.coach.flame.testing.component.api.customer

import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.RegistrationInviteBuilder
import com.coach.flame.jpa.entity.maker.UserMaker
import com.coach.flame.jpa.entity.maker.UserSessionMaker
import com.coach.flame.testing.assertion.http.ErrorAssert
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

class RegisterCustomerClientTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/registerNewCustomerClient.json",
        endpoint = "/api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `test register new client`() {

        // given
        val expirationDate = LocalDateTime.now()
        val user = userMaker
            .but(
                with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.password, "test"),
                with(
                    UserMaker.userSession, userSessionMaker
                        .but(with(UserSessionMaker.expirationDate, expirationDate.plusHours(2)))
                )
            )
            .make()
        val client = clientMaker
            .but(
                with(ClientMaker.firstname, "Nuno"),
                with(ClientMaker.lastname, "Bento"),
                with(ClientMaker.user, user)
            )
            .make()
        val registrationInvite = RegistrationInviteBuilder.default()

        every { clientTypeRepositoryMock.getByType("CLIENT") } returns clientTypeMaker.make()
        every { clientRepositoryMock.saveAndFlush(any()) } returns client

        mockRegistrationInviteRepository.save()
        mockRegistrationInviteRepository.existsByRegistrationKeyIs(
            "OTk5OS0wMS0wMVQxMjowMDowMF90ZXN0QGdtYWlsLmNvbQ==",
            true
        )
        mockRegistrationInviteRepository.findByRegistrationKeyIs(
            "OTk5OS0wMS0wMVQxMjowMDowMF90ZXN0QGdtYWlsLmNvbQ==",
            registrationInvite
        )

        mockClientRepository.findByUuid(client.uuid, client)
        mockClientRepository.save()

        mockCoachRepository.mockFindByUuid(registrationInvite.coach!!.uuid, registrationInvite.coach!!)

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
        then(jsonResponse.getAsJsonPrimitive("firstname").asString).isEqualTo("Nuno")
        then(jsonResponse.getAsJsonPrimitive("lastname").asString).isEqualTo("Bento")
        then(jsonResponse.getAsJsonPrimitive("token").asString).isNotNull
        then(jsonResponse.getAsJsonPrimitive("type").asString).isEqualTo("CLIENT")
        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(client.uuid.toString())

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

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("RestInvalidRequestException.html")
            .hasErrorMessageTitle("RestInvalidRequestException")
            .hasErrorMessageDetail("missing required parameter: lastname")
            .hasErrorMessageStatus("400")
            .hasErrorMessageCode("1001")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/registerNewCustomerClient.json",
        endpoint = "/api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `test register new client duplicated`() {

        every { clientTypeRepositoryMock.getByType("CLIENT") } returns clientTypeMaker.make()
        every { clientRepositoryMock.saveAndFlush(any()) } throws DataIntegrityViolationException("SQL Error -> Duplicate client")
        mockRegistrationInviteRepository.existsByRegistrationKeyIs(
            "OTk5OS0wMS0wMVQxMjowMDowMF90ZXN0QGdtYWlsLmNvbQ==",
            true
        )

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
            .hasErrorMessageTypeEndsWith("CustomerRegisterDuplicateException.html")
            .hasErrorMessageTitle("CustomerRegisterDuplicateException")
            .hasErrorMessageDetail("The following customer already exists.")
            .hasErrorMessageStatus("400")
            .hasErrorMessageCode("2002")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/registerNewCustomerClient.json",
        endpoint = "/api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `test register new client but occurred an internal exception`() {

        every { clientTypeRepositoryMock.getByType("CLIENT") } returns clientTypeMaker.make()
        every { clientRepositoryMock.save(any()) } throws Exception("Ops...something is wrong!")

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

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/registerNewCustomerClientExpirationDate.json",
        endpoint = "/api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `test register new client but registration key expired`() {

        val expirationDate = LocalDateTime.now()
        val user = userMaker
            .but(
                with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.password, "test"),
                with(
                    UserMaker.userSession, userSessionMaker
                        .but(with(UserSessionMaker.expirationDate, expirationDate.plusHours(2)))
                )
            )
            .make()
        val client = clientMaker
            .but(
                with(ClientMaker.firstname, "Nuno"),
                with(ClientMaker.lastname, "Bento"),
                with(ClientMaker.user, user)
            )
            .make()
        every { clientTypeRepositoryMock.getByType("CLIENT") } returns clientTypeMaker.make()
        every { clientRepositoryMock.saveAndFlush(any()) } returns client

        mockRegistrationInviteRepository.existsByRegistrationKeyIs(
            "MTk3MC0wMS0wMVQxMjowMDowMF90ZXN0QGdtYWlsLmNvbQ==",
            true
        )

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
            .hasErrorMessageTypeEndsWith("CustomerRegisterExpirationDate.html")
            .hasErrorMessageTitle("CustomerRegisterExpirationDate")
            .hasErrorMessageDetail("Registration key expired.")
            .hasErrorMessageStatus("400")
            .hasErrorMessageCode("2006")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/registerNewCustomerClient.json",
        endpoint = "/api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `test register new client but registration key is invalid`() {

        val expirationDate = LocalDateTime.now()
        val user = userMaker
            .but(
                with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.password, "test"),
                with(
                    UserMaker.userSession, userSessionMaker
                        .but(with(UserSessionMaker.expirationDate, expirationDate.plusHours(2)))
                )
            )
            .make()
        val client = clientMaker
            .but(
                with(ClientMaker.firstname, "Nuno"),
                with(ClientMaker.lastname, "Bento"),
                with(ClientMaker.user, user)
            )
            .make()
        every { clientTypeRepositoryMock.getByType("CLIENT") } returns clientTypeMaker.make()
        every { clientRepositoryMock.saveAndFlush(any()) } returns client

        mockRegistrationInviteRepository.existsByRegistrationKeyIs(
            "OTk5OS0wMS0wMVQxMjowMDowMF90ZXN0QGdtYWlsLmNvbQ==",
            false
        )

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
            .hasErrorMessageTypeEndsWith("CustomerRegisterWrongRegistrationKey.html")
            .hasErrorMessageTitle("CustomerRegisterWrongRegistrationKey")
            .hasErrorMessageDetail("Registration key invalid.")
            .hasErrorMessageStatus("400")
            .hasErrorMessageCode("2005")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()
    }

}
