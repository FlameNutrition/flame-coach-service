package com.coach.flame.api.user.request

import com.coach.flame.domain.ClientTypeDto
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.with
import com.natpryce.makeiteasy.Maker
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserRequestConverterTest {

    private val classToTest = UserRequestConverter()

    private lateinit var userRequestMaker: Maker<UserRequest>

    @BeforeEach
    fun setUp() {
        userRequestMaker = MakeItEasy.an(UserRequestMaker.UserRequest)
    }

    @Test
    fun `convert request with invalid parameters`() {

        // given
        val userRequest = userRequestMaker
            .but(with(UserRequestMaker.type, "INVALID"))
            .make()

        // when
        val thrown = catchThrowable { classToTest.convert(userRequest) }

        //then
        then(thrown)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Invalid value in type parameter")

    }

    @Test
    fun `convert request successfully`() {

        // given
        val userRequest = userRequestMaker
            .make()

        // when
        val client = classToTest.convert(userRequest)

        //then
        then(client.loginInfo).isNotNull
        then(client.clientType).isEqualTo(ClientTypeDto.CLIENT)
        then(client.firstName).isEqualTo(userRequest.firstname)
        then(client.lastName).isEqualTo(userRequest.lastname)
        then(client.loginInfo!!.username).isEqualTo(userRequest.email)
        then(client.loginInfo!!.password).isEqualTo(userRequest.password)

    }

}