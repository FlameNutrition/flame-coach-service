package com.coach.flame.api.client

import com.coach.flame.api.client.request.EnrollmentRequest
import com.coach.flame.api.client.request.EnrollmentRequestBuilder
import com.coach.flame.api.client.request.EnrollmentRequestMaker
import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.client.ClientEnrollmentProcess
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.domain.maker.CoachDtoBuilder
import com.coach.flame.domain.maker.CoachDtoMaker
import com.coach.flame.exception.RestInvalidRequestException
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

@Suppress("unused")
@ExtendWith(MockKExtension::class)
class EnrollmentCoachApiImplTest {

    @MockK
    private lateinit var customerService: CustomerService

    @MockK
    private lateinit var enrollmentProcess: ClientEnrollmentProcess

    @InjectMockKs
    private lateinit var classToTest: EnrollmentCoachApiImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test init the enrollment process`() {

        val enrollmentProcessRequest = EnrollmentRequestBuilder.maker()
            .but(with(EnrollmentRequestMaker.client, UUID.randomUUID()),
                with(EnrollmentRequestMaker.coach, UUID.randomUUID()))
            .make()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.identifier, enrollmentProcessRequest.client),
                with(ClientDtoMaker.coach, CoachDtoBuilder.maker()
                    .but(with(CoachDtoMaker.identifier, enrollmentProcessRequest.coach))
                    .make()),
                with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
            .make()

        every {
            customerService.getCustomer(enrollmentProcessRequest.client!!, CustomerTypeDto.CLIENT)
        } returns clientDto
        every {
            enrollmentProcess.init(clientDto.copy(clientStatus = ClientStatusDto.AVAILABLE),
                enrollmentProcessRequest.coach!!)
        } returns clientDto

        val response = classToTest.init(enrollmentProcessRequest)

        then(response.client).isEqualTo(enrollmentProcessRequest.client)
        then(response.coach?.identifier).isEqualTo(enrollmentProcessRequest.coach)
        then(response.status).isEqualTo("PENDING")

    }

    @ParameterizedTest(name = "[{index}] init enrollment process missing parameter: {0}")
    @MethodSource("checkInitEnrollmentMandatoryParams")
    fun `test init the enrollment process but missing mandatory params`(param: String) {

        val request: EnrollmentRequest = when (param) {
            "client" -> EnrollmentRequestBuilder.maker()
                .but(with(EnrollmentRequestMaker.coach, UUID.randomUUID()))
                .make()
            "coach" -> EnrollmentRequestBuilder.maker()
                .but(with(EnrollmentRequestMaker.client, UUID.randomUUID()))
                .make()
            else -> EnrollmentRequestBuilder.default()
        }

        val response = catchThrowable { classToTest.init(request) }

        then(response)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("missing required parameter: $param")

    }

    @Test
    fun `test finish the enrollment process`() {

        val enrollmentProcessRequest = EnrollmentRequestBuilder.maker()
            .but(with(EnrollmentRequestMaker.client, UUID.randomUUID()),
                with(EnrollmentRequestMaker.accept, true))
            .make()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.identifier, enrollmentProcessRequest.client),
                with(ClientDtoMaker.coach, CoachDtoBuilder.default()),
                with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()

        every {
            customerService.getCustomer(enrollmentProcessRequest.client!!, CustomerTypeDto.CLIENT)
        } returns clientDto
        every {
            enrollmentProcess.finish(clientDto.copy(clientStatus = ClientStatusDto.PENDING), true)
        } returns clientDto

        val response = classToTest.finish(enrollmentProcessRequest)

        verify { customerService.getCustomer(enrollmentProcessRequest.client!!, CustomerTypeDto.CLIENT) }

        then(response.client).isEqualTo(enrollmentProcessRequest.client)
        then(response.coach?.identifier).isEqualTo(clientDto.coach!!.identifier)
        then(response.coach?.firstName).isEqualTo(clientDto.coach!!.firstName)
        then(response.coach?.lastName).isEqualTo(clientDto.coach!!.lastName)
        then(response.status).isEqualTo("ACCEPTED")

    }

    @ParameterizedTest(name = "[{index}] finish enrollment process missing parameter: {0}")
    @MethodSource("checkFinishEnrollmentMandatoryParams")
    fun `test finish the enrollment process but missing mandatory params`(param: String) {

        val request: EnrollmentRequest = when (param) {
            "client" -> EnrollmentRequestBuilder.maker()
                .but(with(EnrollmentRequestMaker.accept, true))
                .make()
            "accept" -> EnrollmentRequestBuilder.maker()
                .but(with(EnrollmentRequestMaker.client, UUID.randomUUID()))
                .make()
            else -> EnrollmentRequestBuilder.default()
        }

        val response = catchThrowable { classToTest.finish(request) }

        then(response)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("missing required parameter: $param")

    }

    @Test
    fun `test break the enrollment between client and coach`() {

        val enrollmentProcessRequest = EnrollmentRequestBuilder.maker()
            .but(with(EnrollmentRequestMaker.client, UUID.randomUUID())).make()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.identifier, enrollmentProcessRequest.client),
                with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE))
            .make()

        every {
            customerService.getCustomer(enrollmentProcessRequest.client!!, CustomerTypeDto.CLIENT)
        } returns clientDto
        every {
            enrollmentProcess.`break`(clientDto.copy(clientStatus = ClientStatusDto.ACCEPTED))
        } returns clientDto

        val response = classToTest.`break`(enrollmentProcessRequest)

        verify { customerService.getCustomer(enrollmentProcessRequest.client!!, CustomerTypeDto.CLIENT) }

        then(response.client).isEqualTo(enrollmentProcessRequest.client)
        then(response.coach).isNull()
        then(response.status).isEqualTo("AVAILABLE")

    }

    @ParameterizedTest(name = "[{index}] break enrollment between client and coach missing param: {0}")
    @MethodSource("checkBreakEnrollmentMandatoryParams")
    fun `test break the enrollment between client and coach`(param: String) {

        val request: EnrollmentRequest = when (param) {
            "client" -> EnrollmentRequestBuilder.default()
            else -> EnrollmentRequestBuilder.default()
        }

        val response = catchThrowable { classToTest.finish(request) }

        then(response)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("missing required parameter: $param")

    }

    @Test
    fun `test get status enrollment client`() {

        val clientUUID = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.default()

        every { customerService.getCustomer(clientUUID, CustomerTypeDto.CLIENT) } returns clientDto

        val result = classToTest.status(clientUUID)

        then(result.status).isEqualTo("AVAILABLE")

    }

    // region Parameters

    companion object {
        @JvmStatic
        fun checkInitEnrollmentMandatoryParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("client"),
                Arguments.of("coach")
            )
        }

        @JvmStatic
        fun checkFinishEnrollmentMandatoryParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("client"),
                Arguments.of("accept"),
            )
        }

        @JvmStatic
        fun checkBreakEnrollmentMandatoryParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("client"),
            )
        }
    }

    // endregion

}
