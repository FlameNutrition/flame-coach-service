package com.coach.flame.api.client

import com.coach.flame.api.client.request.MeasureRequest
import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.measures.MeasureFactory
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.MeasureDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.domain.maker.MeasureDtoBuilder
import com.coach.flame.exception.RestInvalidRequestException
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class MeasureApiImplTest {

    @MockK
    private lateinit var customerService: CustomerService

    @MockK
    private lateinit var measureFactory: MeasureFactory

    @InjectMockKs
    private lateinit var classToTest: MeasureApiImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test add weight`() {

        val client = ClientDtoBuilder.makerWithLoginInfo()
            .make()
        val request = MeasureRequest(
            value = 54.6f,
            date = null)
        val measureDto = slot<MeasureDto>()

        every { customerService.getCustomer(client.identifier, CustomerTypeDto.CLIENT) } returns client
        every {
            measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.ADD,
                client,
                capture(measureDto))
        } answers {
            client.weightMeasureTimeline.add(measureDto.captured.copy(id = 100L))
            client
        }

        val response = classToTest.addWeight(request, client.identifier)

        then(response.weights).isNotNull
        then(response.weights).isNotEmpty
        then(response.weights?.first()?.value).isEqualTo(54.6f)
        then(response.weights?.first()?.date).isNotNull
    }

    @Test
    fun `test add weight with other weights`() {

        val listOfWeights = mutableListOf(MeasureDtoBuilder.makerWithId().make(),
            MeasureDtoBuilder.makerWithId().make(),
            MeasureDtoBuilder.makerWithId().make())

        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.listOfWeights, listOfWeights))
            .make()
        val request = MeasureRequest(
            value = 54.6f,
            date = null)
        val measureDto = slot<MeasureDto>()

        every { customerService.getCustomer(client.identifier, CustomerTypeDto.CLIENT) } returns client
        every {
            measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.ADD,
                client,
                capture(measureDto))
        } answers {
            client.weightMeasureTimeline.add(measureDto.captured.copy(id = 100L))
            client
        }

        val response = classToTest.addWeight(request, client.identifier)

        then(response.weights).isNotNull
        then(response.weights).hasSize(4)

    }

    @Test
    fun `test add weight with date`() {

        val client = ClientDtoBuilder.makerWithLoginInfo()
            .make()
        val date = LocalDate.now().plusDays(1)
        val request = MeasureRequest(
            value = 54.6f,
            date = date)
        val measureDto = slot<MeasureDto>()

        every { customerService.getCustomer(client.identifier, CustomerTypeDto.CLIENT) } returns client
        every {
            measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.ADD,
                client,
                capture(measureDto))
        } answers {
            client.weightMeasureTimeline.add(measureDto.captured.copy(id = 100L))
            client
        }

        val response = classToTest.addWeight(request, client.identifier)

        then(response.weights).isNotNull
        then(response.weights).isNotEmpty
        then(response.weights?.first()?.date).isEqualTo(date)

    }

    @Test
    fun `test edit weight with others weights`() {

        val listOfWeights = mutableListOf(MeasureDtoBuilder.makerWithId().make(),
            MeasureDtoBuilder.makerWithId().make(),
            MeasureDtoBuilder.makerWithId().make())

        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.listOfWeights, listOfWeights))
            .make()
        val request = MeasureRequest(
            value = 54.6f,
            date = null)
        val measureDto = slot<MeasureDto>()

        every { customerService.getCustomer(client.identifier, CustomerTypeDto.CLIENT) } returns client
        every {
            measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.UPDATE,
                client,
                capture(measureDto))
        } answers {
            client.weightMeasureTimeline.remove(listOfWeights.first())
            client.weightMeasureTimeline.add(measureDto.captured.copy(id = 1L))
            client
        }

        val response = classToTest.editWeight(request, client.identifier, 1L)

        then(response.weights).isNotNull
        then(response.weights).hasSize(3)
        then(response.weights?.find { it.identifier == 1L }?.value).isEqualTo(54.6f)

    }

    @Test
    fun `test delete weight with others weights`() {

        val listOfWeights = mutableListOf(MeasureDtoBuilder.makerWithId().make(),
            MeasureDtoBuilder.makerWithId().make(),
            MeasureDtoBuilder.makerWithId().make())

        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.listOfWeights, listOfWeights))
            .make()
        val measureDto = slot<MeasureDto>()

        every { customerService.getCustomer(client.identifier, CustomerTypeDto.CLIENT) } returns client
        every {
            measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.DELETE,
                client,
                capture(measureDto))
        } answers {
            client.weightMeasureTimeline.remove(listOfWeights.first())
            client
        }

        val response = classToTest.deleteWeight(client.identifier, 1L)

        then(response.weights).isNotNull
        then(response.weights).hasSize(2)
        then(response.weights?.find { it.identifier == 1L }).isNull()

    }

    @Test
    fun `test get weights with others weights`() {

        val listOfWeights = mutableListOf(MeasureDtoBuilder.makerWithId().make(),
            MeasureDtoBuilder.makerWithId().make(),
            MeasureDtoBuilder.makerWithId().make())

        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.listOfWeights, listOfWeights))
            .make()

        every { customerService.getCustomer(client.identifier, CustomerTypeDto.CLIENT) } returns client
        every {
            measureFactory.executeOperation(MeasureFactory.Measure.WEIGHT,
                MeasureFactory.Operation.GET,
                client,
                null)
        } answers {
            client
        }

        val response = classToTest.getWeights(client.identifier)

        then(response.weights).isNotNull
        then(response.weights).hasSize(3)

    }

    @ParameterizedTest(name = "[{index}] test illegal arguments when missing parameter: {1}")
    @MethodSource("checkMandatoryParametersGetWeights")
    fun `test get weight illegal arguments when missing mandatory parameters`(
        clientIdentifier: UUID?,
        missingParam: String,
    ) {

        val response = catchThrowable { classToTest.getWeights(clientIdentifier) }

        then(response).isInstanceOf(RestInvalidRequestException::class.java)
        then(response).hasMessageContaining("missing required parameter: ")
        then(response).hasMessageContaining(missingParam)

    }

    @ParameterizedTest(name = "[{index}] test illegal arguments when missing parameter: {2}")
    @MethodSource("checkMandatoryParametersAddWeight")
    fun `test add weight illegal arguments when missing mandatory parameters`(
        request: MeasureRequest,
        clientIdentifier: UUID?,
        missingParam: String,
    ) {

        val response = catchThrowable { classToTest.addWeight(request, clientIdentifier) }

        then(response).isInstanceOf(RestInvalidRequestException::class.java)
        then(response).hasMessageContaining("missing required parameter: ")
        then(response).hasMessageContaining(missingParam)

    }

    @ParameterizedTest(name = "[{index}] test illegal arguments when missing parameter: {3}")
    @MethodSource("checkMandatoryParametersEditWeight")
    fun `test edit weight illegal arguments when missing mandatory parameters`(
        request: MeasureRequest,
        clientIdentifier: UUID?,
        identifier: Long?,
        missingParam: String,
    ) {

        val response = catchThrowable { classToTest.editWeight(request, clientIdentifier, identifier) }

        then(response).isInstanceOf(RestInvalidRequestException::class.java)
        then(response).hasMessageContaining("missing required parameter: ")
        then(response).hasMessageContaining(missingParam)

    }

    @ParameterizedTest(name = "[{index}] test illegal arguments when missing parameter: {2}")
    @MethodSource("checkMandatoryParametersDeleteWeight")
    fun `test delete weight illegal arguments when missing mandatory parameters`(
        clientIdentifier: UUID?,
        identifier: Long?,
        missingParam: String,
    ) {

        val response = catchThrowable { classToTest.deleteWeight(clientIdentifier, identifier) }

        then(response).isInstanceOf(RestInvalidRequestException::class.java)
        then(response).hasMessageContaining("missing required parameter: ")
        then(response).hasMessageContaining(missingParam)

    }

    // region Parameters

    companion object {

        @JvmStatic
        fun checkMandatoryParametersGetWeights(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(null, "uuid"),
            )
        }

        @JvmStatic
        fun checkMandatoryParametersAddWeight(): Stream<Arguments> {
            val requestMissingUUID = MeasureRequest(54.6f, LocalDate.now())
            val requestMissingValue = MeasureRequest(null, LocalDate.now())

            return Stream.of(
                Arguments.of(requestMissingUUID, null, "uuid"),
                Arguments.of(requestMissingValue, UUID.randomUUID(), "value")
            )
        }

        @JvmStatic
        fun checkMandatoryParametersEditWeight(): Stream<Arguments> {
            val requestMissingUUID = MeasureRequest(54.6f, LocalDate.now())
            val requestMissingValue = MeasureRequest(null, LocalDate.now())
            val requestMissingIdentifier = MeasureRequest(54.6f, LocalDate.now())

            return Stream.of(
                Arguments.of(requestMissingUUID, null, 100L, "uuid"),
                Arguments.of(requestMissingValue, UUID.randomUUID(), 100L, "value"),
                Arguments.of(requestMissingIdentifier, UUID.randomUUID(), null, "identifier"),
            )
        }

        @JvmStatic
        fun checkMandatoryParametersDeleteWeight(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(null, 100L, "uuid"),
                Arguments.of(UUID.randomUUID(), null, "identifier"),
            )
        }
    }

    // endregion

}
