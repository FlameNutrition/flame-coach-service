package com.coach.flame.api.client.request

import com.coach.flame.domain.ClientTypeDto
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.with
import com.natpryce.makeiteasy.Maker
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClientRequestConverterTest {

    private val classToTest = ClientRequestConverter()

    private lateinit var clientRequestMaker: Maker<ClientRequest>

    @BeforeEach
    fun setUp() {
        clientRequestMaker = MakeItEasy.an(UserRequestMaker.ClientRequest)
    }

    @Test
    fun `convert request with invalid parameters`() {

        // given
        val userRequest = clientRequestMaker
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
        val userRequest = clientRequestMaker
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