package com.coach.flame.api.customer

import com.coach.flame.api.customer.request.CustomerRequest
import com.coach.flame.api.customer.request.CustomerRequestConverter
import com.coach.flame.api.customer.request.CustomerRequestMaker
import com.coach.flame.api.customer.request.CustomerRequestMaker.Companion.CustomerRequest
import com.coach.flame.api.customer.request.CustomerRequestMaker.Companion.type
import com.coach.flame.api.customer.request.UpdatePasswordRequest
import com.coach.flame.api.customer.response.CustomerResponse
import com.coach.flame.api.customer.response.CustomerResponseConverter
import com.coach.flame.api.customer.response.CustomerResponseMaker
import com.coach.flame.api.customer.response.CustomerResponseMaker.Companion.CustomerResponse
import com.coach.flame.customer.CustomerService
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.LoginInfoDto
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.domain.maker.ClientDtoMaker.Companion.ClientDto
import com.coach.flame.domain.maker.LoginInfoDtoMaker
import com.coach.flame.domain.maker.LoginInfoDtoMaker.Companion.LoginInfoDto
import com.coach.flame.exception.RestInvalidRequestException
import com.natpryce.makeiteasy.MakeItEasy.*
import com.natpryce.makeiteasy.Maker
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class CustomerImpTest {

    @MockK
    private lateinit var customerService: CustomerService

    @MockK
    private lateinit var customerRequestConverter: CustomerRequestConverter

    @SpyK
    private var customerResponseConverter: CustomerResponseConverter = CustomerResponseConverter()

    @InjectMockKs
    private lateinit var classToTest: CustomerImp

    private lateinit var customerRequestMaker: Maker<CustomerRequest>
    private lateinit var customerResponseMaker: Maker<CustomerResponse>
    private lateinit var clientDtoMaker: Maker<ClientDto>
    private lateinit var loginInfoDtoMaker: Maker<LoginInfoDto>

    @BeforeEach
    fun setUp() {
        customerRequestMaker = an(CustomerRequest)
        customerResponseMaker = an(CustomerResponse)
        clientDtoMaker = an(ClientDto)
        loginInfoDtoMaker = an(LoginInfoDto)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    // region Register new client

    @ParameterizedTest(name = "[{index}] register new customer missing parameter: {0}")
    @MethodSource("userRegisterMandatoryParameters")
    fun `register new customer with missing mandatory parameters`(missingParam: String) {

        // given
        val userRequestMakerCopy = when (missingParam) {
            "firstname" -> customerRequestMaker
                .but(with(CustomerRequestMaker.firstName, null as String?))
                .make()
            "lastname" -> customerRequestMaker
                .but(with(CustomerRequestMaker.lastName, null as String?))
                .make()
            "email" -> customerRequestMaker
                .but(with(CustomerRequestMaker.email, null as String?))
                .make()
            "password" -> customerRequestMaker
                .but(with(CustomerRequestMaker.password, null as String?))
                .make()
            "type" -> customerRequestMaker
                .but(with(type, null as String?))
                .make()
            else -> customerRequestMaker.make()
        }

        // when
        val thrown = catchThrowable { classToTest.registerNewCustomer(userRequestMakerCopy) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("missing required parameter: $missingParam")

    }

    @Test
    fun `register new customer with illegal state`() {

        // given
        val userRequest = customerRequestMaker.but(
            with(CustomerRequestMaker.registrationKey, "KEY"),
            with(type, "INVALID")).make()
        every { customerRequestConverter.convert(userRequest) } throws IllegalArgumentException("Invalid parameter request: type")

        // when
        val thrown = catchThrowable { classToTest.registerNewCustomer(userRequest) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("Invalid parameter request: type")
    }

    @Test
    fun `register new customer with unexpected error`() {

        // given
        val userRequest = customerRequestMaker.but(
            with(CustomerRequestMaker.registrationKey, "KEY")
        ).make()
        val clientDto = clientDtoMaker.make()
        every { customerRequestConverter.convert(userRequest) } returns clientDto
        every { customerService.registerCustomer(clientDto) } throws RuntimeException("Something wrong happened")

        // when
        val thrown = catchThrowable { classToTest.registerNewCustomer(userRequest) }

        //then
        then(thrown)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Something wrong happened")
    }

    @Test
    fun `register new customer successfully`() {

        // given
        val customerRequest = customerRequestMaker.but(with(CustomerRequestMaker.registrationKey, "KEY"))
            .make()
        val preClientDto = clientDtoMaker
            .but(with(ClientDtoMaker.loginInfo, null as LoginInfoDto?))
            .make()
        val postClientDto = clientDtoMaker
            .but(with(ClientDtoMaker.loginInfo, loginInfoDtoMaker
                .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                    with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
                .make()))
            .make()
        every { customerRequestConverter.convert(customerRequest) } returns preClientDto
        every { customerService.registerCustomer(preClientDto) } returns postClientDto

        // when
        val response = classToTest.registerNewCustomer(customerRequest)

        //then
        then(response).isNotNull
        then(response.firstname).isEqualTo(postClientDto.firstName)
        then(response.lastname).isEqualTo(postClientDto.lastName)
        then(response.username).isEqualTo(postClientDto.loginInfo!!.username)
        then(response.expiration).isEqualTo(postClientDto.loginInfo!!.expirationDate)
        then(response.token).isEqualTo(postClientDto.loginInfo!!.token)
        then(response.identifier).isEqualTo(postClientDto.identifier)

    }

    // endregion

    // region Get new client session

    @Test
    fun `get a new customer session`() {

        // given
        val customerRequest = customerRequestMaker
            .but(with(CustomerRequestMaker.email, "test@test.com"))
            .but(with(CustomerRequestMaker.password, "12345"))
            .make()
        val postClientDto = clientDtoMaker
            .but(with(ClientDtoMaker.loginInfo,
                make(a(
                    LoginInfoDto,
                    with(LoginInfoDtoMaker.username, "test@test.com"),
                    with(LoginInfoDtoMaker.password, "12345"),
                    with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()),
                    with(LoginInfoDtoMaker.token, UUID.randomUUID())
                ))
            )).make()
        val customerResponse = customerResponseMaker
            .but(
                with(CustomerResponseMaker.username, "test@test.com"),
                with(CustomerResponseMaker.firstName, postClientDto.firstName),
                with(CustomerResponseMaker.lastName, postClientDto.lastName),
                with(CustomerResponseMaker.expiration, postClientDto.loginInfo?.expirationDate),
                with(CustomerResponseMaker.token, postClientDto.loginInfo?.token),
            )
            .make()

        every { customerService.getNewCustomerSession("test@test.com", "12345") } returns postClientDto
        every { customerResponseConverter.convert(any()) } returns customerResponse

        // when
        val response = classToTest.getNewCustomerSession(customerRequest)

        // then
        then(response).isNotNull
        then(response.firstname).isEqualTo(postClientDto.firstName)
        then(response.lastname).isEqualTo(postClientDto.lastName)
        then(response.username).isEqualTo(postClientDto.loginInfo?.username)
        then(response.expiration).isEqualTo(postClientDto.loginInfo?.expirationDate)
        then(response.token).isEqualTo(postClientDto.loginInfo?.token)

    }

    @ParameterizedTest(name = "[{index}] get customer session missing parameter: {0}")
    @MethodSource("getCustomerSessionMandatoryParameters")
    fun `get a new customer session with missing mandatory parameters`(missingParam: String) {

        // given
        val userRequestMakerCopy = when (missingParam) {
            "email" -> customerRequestMaker
                .but(with(CustomerRequestMaker.email, null as String?))
                .make()
            "password" -> customerRequestMaker
                .but(with(CustomerRequestMaker.password, null as String?))
                .make()
            else -> customerRequestMaker.make()
        }

        // when
        val thrown = catchThrowable { classToTest.getNewCustomerSession(userRequestMakerCopy) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("Missing required parameter request: $missingParam")

    }

    // endregion

    // region Update

    @Test
    fun `update customer password`() {

        // given
        val request = UpdatePasswordRequest("test@gmail.com", "OLD", "NEW")

        every { customerService.updateCustomerPassword("test@gmail.com", "OLD", "NEW") } returns mockk()

        // when
        val response = classToTest.updatePassword(request)

        // then
        then(response.result).isTrue

    }

    // endregion

    // region Parameters

    companion object {
        @JvmStatic
        fun userRegisterMandatoryParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("firstname"),
                Arguments.of("lastname"),
                Arguments.of("email"),
                Arguments.of("password"),
                Arguments.of("type"),
            )
        }

        @JvmStatic
        fun getCustomerSessionMandatoryParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("email"),
                Arguments.of("password"),
            )
        }
    }

    // endregion


}
