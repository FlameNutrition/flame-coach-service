package com.coach.flame.customer.measures

import com.coach.flame.domain.MeasureTypeDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.domain.maker.MeasureDtoBuilder
import com.coach.flame.domain.maker.MeasureDtoMaker
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.repository.ClientRepository
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

@ExtendWith(MockKExtension::class)
class WeightServiceImplTest {

    @MockK
    private lateinit var clientRepository: ClientRepository

    @InjectMockKs
    private lateinit var classToTest: WeightServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get weights client using kg`() {

        val weight = MeasureDtoBuilder.maker()
            .but(with(MeasureDtoMaker.value, 80.50f))
            .make()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.measureType, MeasureTypeDto.KG_CM),
                with(ClientDtoMaker.listOfWeights, mutableListOf(weight)))
            .make()

        val result = classToTest.get(clientDto)

        then(result.weightMeasureTimeline).isNotEmpty
        then(result.weightMeasureTimeline).hasSize(1)
        then(result.weightMeasureTimeline.first().value).isEqualTo(80.50f)

    }

    @Test
    fun `test get weights client using lbs`() {

        val weight = MeasureDtoBuilder.maker()
            .but(with(MeasureDtoMaker.value, 75.39f))
            .make()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.measureType, MeasureTypeDto.LBS_IN),
                with(ClientDtoMaker.listOfWeights, mutableListOf(weight)))
            .make()

        val result = classToTest.get(clientDto)

        then(result.weightMeasureTimeline).isNotEmpty
        then(result.weightMeasureTimeline).hasSize(1)
        then(result.weightMeasureTimeline.first().value).isEqualTo(166.2f)

    }

    @Test
    fun `test add new weight client using kg`() {

        val weight = MeasureDtoBuilder.maker()
            .but(with(MeasureDtoMaker.value, 75.39f))
            .make()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo().make()
        val client = slot<Client>()

        every { clientRepository.saveAndFlush(capture(client)) } answers { client.captured }

        val result = classToTest.add(clientDto, weight)

        then(result.weightMeasureTimeline).isNotEmpty
        then(result.weightMeasureTimeline).hasSize(1)
        then(result.weightMeasureTimeline.first().value).isEqualTo(75.39f)

    }

    @Test
    fun `test add new weight client using lbs`() {

        val weight = MeasureDtoBuilder.maker()
            .but(with(MeasureDtoMaker.value, 166.2f))
            .make()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.measureType, MeasureTypeDto.LBS_IN))
            .make()
        val client = slot<Client>()

        every { clientRepository.saveAndFlush(capture(client)) } answers { client.captured }

        val result = classToTest.add(clientDto, weight)

        then(client.captured.clientMeasureWeight.first().weight).isEqualTo(75.39f)

        then(result.weightMeasureTimeline).isNotEmpty
        then(result.weightMeasureTimeline).hasSize(1)
        then(result.weightMeasureTimeline.first().value).isEqualTo(166.2f)

    }

    @Test
    fun `test update weight client using kg`() {

        val weight0 = MeasureDtoBuilder.makerWithId()
            .but(with(MeasureDtoMaker.value, 75.39f))
            .make()
        val weight1 = MeasureDtoBuilder.makerWithId()
            .but(with(MeasureDtoMaker.value, 80.2f))
            .make()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.listOfWeights, mutableListOf(weight0, weight1)))
            .make()
        val client = slot<Client>()

        every { clientRepository.saveAndFlush(capture(client)) } answers { client.captured }

        val result = classToTest.update(clientDto, weight1.copy(value = 76.5f))

        then(result.weightMeasureTimeline).isNotEmpty
        then(result.weightMeasureTimeline).hasSize(2)
        then(result.weightMeasureTimeline.find { it.id == weight0.id }?.value).isEqualTo(75.39f)
        then(result.weightMeasureTimeline.find { it.id == weight1.id }?.value).isEqualTo(76.5f)

    }

    @Test
    fun `test update weight client using lbs`() {

        val weight0 = MeasureDtoBuilder.makerWithId()
            .but(with(MeasureDtoMaker.value, 75.39f))
            .make()
        val weight1 = MeasureDtoBuilder.makerWithId()
            .but(with(MeasureDtoMaker.value, 77.2f))
            .make()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.measureType, MeasureTypeDto.LBS_IN),
                with(ClientDtoMaker.listOfWeights, mutableListOf(weight0, weight1)))
            .make()
        val client = slot<Client>()

        every { clientRepository.saveAndFlush(capture(client)) } answers { client.captured }

        val result = classToTest.update(clientDto, weight1.copy(value = 167.5f))

        then(client.captured.clientMeasureWeight.map { it.toDto() }
            .find { it.id == weight1.id }?.value).isEqualTo(75.98f)

        then(result.weightMeasureTimeline).isNotEmpty
        then(result.weightMeasureTimeline).hasSize(2)
        then(result.weightMeasureTimeline.find { it.id == weight0.id }?.value).isEqualTo(166.2f)
        then(result.weightMeasureTimeline.find { it.id == weight1.id }?.value).isEqualTo(167.51f)

    }

    @Test
    fun `test delete weight invalid id`() {

        val weight0 = MeasureDtoBuilder.makerWithId().make()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.measureType, MeasureTypeDto.KG_CM),
                with(ClientDtoMaker.listOfWeights, mutableListOf(weight0)))
            .make()

        val result = catchThrowable { classToTest.delete(clientDto, weight0.copy(id = 100L)) }

        then(result).isInstanceOf(MeasureNotFoundException::class.java)
            .hasMessageContaining("Measure is not present in the list")

    }

    @Test
    fun `test delete weight invalid id repository exception`() {

        val weight0 = MeasureDtoBuilder.makerWithId().make()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.measureType, MeasureTypeDto.KG_CM),
                with(ClientDtoMaker.listOfWeights, mutableListOf(weight0)))
            .make()

        every { clientRepository.saveAndFlush(any()) } throws IllegalArgumentException("FAIL")

        val result = catchThrowable { classToTest.delete(clientDto, weight0) }

        then(result).isInstanceOf(MeasureNotFoundException::class.java)
            .hasMessageContaining("Did not found any measure with the provided identifier")

    }

    @Test
    fun `test delete weight`() {

        val weight0 = MeasureDtoBuilder.makerWithId()
            .but(with(MeasureDtoMaker.value, 75.39f))
            .make()
        val weight1 = MeasureDtoBuilder.makerWithId()
            .but(with(MeasureDtoMaker.value, 77.2f))
            .make()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.measureType, MeasureTypeDto.KG_CM),
                with(ClientDtoMaker.listOfWeights, mutableListOf(weight0, weight1)))
            .make()
        val client = slot<Client>()

        every { clientRepository.saveAndFlush(capture(client)) } answers { client.captured }

        val result = classToTest.delete(clientDto, weight0)

        then(result.weightMeasureTimeline).isNotEmpty
        then(result.weightMeasureTimeline).hasSize(1)

    }


}
