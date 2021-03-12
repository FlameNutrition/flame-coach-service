package com.coach.flame.api.customer.response

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientDtoMaker
import com.coach.flame.domain.LoginInfoDto
import com.coach.flame.domain.LoginInfoDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.*
import com.natpryce.makeiteasy.Maker
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream

class CustomerResponseConverterTest {

    private val classToTest = CustomerResponseConverter()

    private lateinit var clientDtoMaker: Maker<ClientDto>
    private lateinit var loginInfoDtoMaker: Maker<LoginInfoDto>

    @BeforeEach
    fun setUp() {
        clientDtoMaker = an(ClientDtoMaker.ClientDto)
        loginInfoDtoMaker = an(LoginInfoDtoMaker.LoginInfoDto)
    }

    @ParameterizedTest(name = "[{index}] convert response with illegal state: {1}")
    @MethodSource("illegalArgumentsExceptions")
    fun `convert response with illegal arguments`(clientDto: ClientDto, illegalValue: String) {

        // when
        val thrown = catchThrowable { classToTest.convert(clientDto) }

        //then
        then(thrown)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("$illegalValue should not be null")

    }

    @Test
    fun `convert response successfully`() {

        // given
        val clientDto = clientDtoMaker
            .but(with(ClientDtoMaker.loginInfo, loginInfoDtoMaker
                .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                    with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
                .make()))
            .make()

        // when
        val response = classToTest.convert(clientDto)

        //then
        then(response).isNotNull
        then(response.firstname).isEqualTo(clientDto.firstName)
        then(response.lastname).isEqualTo(clientDto.lastName)
        then(response.token).isEqualTo(clientDto.loginInfo!!.token)
        then(response.expiration).isEqualTo(clientDto.loginInfo!!.expirationDate)
        then(response.username).isEqualTo(clientDto.loginInfo!!.username)
        then(response.type).isEqualTo(clientDto.customerType.name)
        then(response.identifier).isEqualTo(clientDto.identifier)

    }

    // region Parameters

    companion object {
        @JvmStatic
        fun illegalArgumentsExceptions(): Stream<Arguments> {

            val loginInfoMaker = an(LoginInfoDtoMaker.LoginInfoDto)

            val loginInfoNull = make(a(ClientDtoMaker.ClientDto,
                with(ClientDtoMaker.loginInfo, null as LoginInfoDto?)))

            val loginInfoTokenNull = make(a(ClientDtoMaker.ClientDto,
                with(ClientDtoMaker.loginInfo,
                    loginInfoMaker
                        .but(with(LoginInfoDtoMaker.token, null as UUID?),
                            with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
                        .make())))

            val loginInfoExpirationNull = make(a(ClientDtoMaker.ClientDto,
                with(ClientDtoMaker.loginInfo,
                    loginInfoMaker
                        .but(with(LoginInfoDtoMaker.expirationDate, null as LocalDateTime?),
                            with(LoginInfoDtoMaker.token, UUID.randomUUID()))
                        .make())))

            return Stream.of(
                Arguments.of(loginInfoNull, "loginInfo"),
                Arguments.of(loginInfoTokenNull, "loginInfo->token"),
                Arguments.of(loginInfoExpirationNull, "loginInfo->expirationDate"),
            )
        }
    }

    // endregion

}