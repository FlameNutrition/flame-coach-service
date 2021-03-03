package com.coach.flame.api.client

import com.coach.flame.api.client.request.ClientRequest
import com.coach.flame.api.client.request.ClientRequestConverter
import com.coach.flame.api.client.request.UserRequestMaker
import com.coach.flame.api.client.request.UserRequestMaker.Companion.ClientRequest
import com.coach.flame.api.client.request.UserRequestMaker.Companion.TYPE
import com.coach.flame.api.client.response.ClientResponse
import com.coach.flame.api.client.response.ClientResponseConverter
import com.coach.flame.api.client.response.UserResponseMaker
import com.coach.flame.api.client.response.UserResponseMaker.Companion.ClientResponse
import com.coach.flame.client.ClientService
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientDtoMaker
import com.coach.flame.domain.ClientDtoMaker.Companion.ClientDto
import com.coach.flame.domain.LoginInfoDto
import com.coach.flame.exception.RestInvalidRequestException
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.MakeItEasy.with
import com.natpryce.makeiteasy.Maker
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class ClientImpTest {

    @MockK
    private lateinit var clientService: ClientService

    @MockK
    private lateinit var clientRequestConverter: ClientRequestConverter

    @MockK
    private lateinit var clientResponseConverter: ClientResponseConverter

    @InjectMockKs
    private lateinit var classToTest: ClientImp

    private lateinit var clientRequestMaker: Maker<ClientRequest>

    private lateinit var clientResponseMaker: Maker<ClientResponse>

    private lateinit var clientDtoMaker: Maker<ClientDto>

    @BeforeEach
    fun setUp() {
        clientRequestMaker = an(ClientRequest)
        clientResponseMaker = an(ClientResponse)
        clientDtoMaker = an(ClientDto)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @ParameterizedTest(name = "[{index}] register new user missing parameter: {0}")
    @MethodSource("userRegisterMandatoryParameters")
    fun `register new user with missing mandatory parameters`(missingParam: String) {

        // given
        val userRequestMakerCopy = when (missingParam) {
            "firstname" -> clientRequestMaker
                .but(with(UserRequestMaker.FIRST_NAME, null as String?))
                .make()
            "lastname" -> clientRequestMaker
                .but(with(UserRequestMaker.LASTNAME, null as String?))
                .make()
            "email" -> clientRequestMaker
                .but(with(UserRequestMaker.EMAIL, null as String?))
                .make()
            "password" -> clientRequestMaker
                .but(with(UserRequestMaker.PASSWORD, null as String?))
                .make()
            "type" -> clientRequestMaker
                .but(with(TYPE, null as String?))
                .make()
            else -> clientRequestMaker.make()
        }

        // when
        val thrown = catchThrowable { classToTest.registerNewClient(userRequestMakerCopy) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("Missing required parameter request: $missingParam")

    }

    @Test
    fun `register new user with illegal state`() {

        // given
        val userRequest = clientRequestMaker.but(with(TYPE, "INVALID")).make()
        every { clientRequestConverter.convert(userRequest) } throws IllegalArgumentException("Invalid parameter request: type")

        // when
        val thrown = catchThrowable { classToTest.registerNewClient(userRequest) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("Invalid parameter request: type")
    }

    @Test
    fun `register new user with unexpected error`() {

        // given
        val userRequest = clientRequestMaker.make()
        val clientDto = clientDtoMaker.make()
        every { clientRequestConverter.convert(userRequest) } returns clientDto
        every { clientService.registerClient(clientDto) } throws RuntimeException("Something wrong happened")

        // when
        val thrown = catchThrowable { classToTest.registerNewClient(userRequest) }

        //then
        then(thrown)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Something wrong happened")
    }

    @Test
    fun `register new user successfully`() {

        // given
        val userRequest = clientRequestMaker.make()
        val preClientDto = clientDtoMaker
            .but(with(ClientDtoMaker.loginInfo, null as LoginInfoDto?))
            .make()
        val postClientDto = clientDtoMaker
            .make()
        val userResponse = clientResponseMaker
            .but(
                with(UserResponseMaker.USERNAME, postClientDto.loginInfo!!.username),
                with(UserResponseMaker.FIRSTNAME, postClientDto.firstName),
                with(UserResponseMaker.LASTNAME, postClientDto.lastName),
                with(UserResponseMaker.EXPIRATION, postClientDto.loginInfo!!.expirationDate),
                with(UserResponseMaker.TOKEN, postClientDto.loginInfo!!.token),
            )
            .make()
        every { clientRequestConverter.convert(userRequest) } returns preClientDto
        every { clientService.registerClient(preClientDto) } returns postClientDto
        every { clientResponseConverter.convert(postClientDto) } returns userResponse

        // when
        val response = classToTest.registerNewClient(userRequest)

        //then
        then(response).isNotNull
        then(response.firstname).isEqualTo(postClientDto.firstName)
        then(response.lastname).isEqualTo(postClientDto.lastName)
        then(response.username).isEqualTo(postClientDto.loginInfo!!.username)
        then(response.expiration).isEqualTo(postClientDto.loginInfo!!.expirationDate)
        then(response.token).isEqualTo(postClientDto.loginInfo!!.token)

    }

    // region Parameters

    companion object {
        @JvmStatic
        fun userRegisterMandatoryParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("firstname"),
                Arguments.of("lastname"),
                Arguments.of("email"),
                Arguments.of("password"),
                Arguments.of("type")
            )
        }
    }

    // endregion


}