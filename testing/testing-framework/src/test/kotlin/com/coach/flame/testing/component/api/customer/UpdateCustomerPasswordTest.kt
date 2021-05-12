package com.coach.flame.testing.component.api.customer

import com.coach.flame.jpa.entity.User
import com.coach.flame.jpa.entity.maker.UserBuilder
import com.coach.flame.jpa.entity.maker.UserMaker
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod

class UpdateCustomerPasswordTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/updateCustomerPassword.json",
        endpoint = "/api/customer/updatePassword",
        httpMethod = RequestMethod.POST
    )
    fun `test update customer password`() {

        // given
        val salt = saltTool.generate()
        val oldHashPassword = hashPasswordTool.generate("12345", salt)
        val user = UserBuilder.maker()
            .but(with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.key, salt),
                with(UserMaker.password, oldHashPassword))
            .make()
        val entityCaptured = slot<User>()
        every { userRepositoryMock.findUserByEmail("test@gmail.com") } returns user
        every { userRepositoryMock.saveAndFlush(capture(entityCaptured)) } returns mockk()

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

        then(jsonResponse.getAsJsonPrimitive("result").asBoolean).isTrue

        then(entityCaptured.captured.keyDecrypt).isNotEqualTo(salt)
        then(entityCaptured.captured.password).isNotEqualTo(oldHashPassword)


    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/customer/updateCustomerPasswordMissingParam.json",
        endpoint = "/api/customer/updatePassword",
        httpMethod = RequestMethod.POST
    )
    fun `test update customer password but missing request parameter`() {

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
        thenErrorCode(body).isEqualTo("9999")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()


    }


}
