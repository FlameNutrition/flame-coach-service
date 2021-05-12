package com.coach.flame.customer.measures

import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.MeasureDtoBuilder
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MeasureFactoryTest {

    @MockK(relaxed = true)
    private lateinit var weightServiceImpl: WeightServiceImpl

    @InjectMockKs
    private lateinit var classToTest: MeasureFactory

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test check execute get weight`() {

        val client = ClientDtoBuilder.makerWithLoginInfo().make()
        val measure = MeasureDtoBuilder.default()
        classToTest.executeOperation(MeasureFactory.Measure.WEIGHT,
            MeasureFactory.Operation.GET, client, measure)

        verify(exactly = 1) { weightServiceImpl.get(client) }

    }

    @Test
    fun `test check execute add weight`() {

        val client = ClientDtoBuilder.makerWithLoginInfo().make()
        val measure = MeasureDtoBuilder.default()
        classToTest.executeOperation(MeasureFactory.Measure.WEIGHT,
            MeasureFactory.Operation.ADD, client, measure)

        verify(exactly = 1) { weightServiceImpl.add(client, measure) }

    }

    @Test
    fun `test check execute update weight`() {

        val client = ClientDtoBuilder.makerWithLoginInfo().make()
        val measure = MeasureDtoBuilder.default()
        classToTest.executeOperation(MeasureFactory.Measure.WEIGHT,
            MeasureFactory.Operation.UPDATE, client, measure)

        verify(exactly = 1) { weightServiceImpl.update(client, measure) }

    }

    @Test
    fun `test check execute delete weight`() {

        val client = ClientDtoBuilder.makerWithLoginInfo().make()
        val measure = MeasureDtoBuilder.default()
        classToTest.executeOperation(MeasureFactory.Measure.WEIGHT,
            MeasureFactory.Operation.DELETE, client, measure)

        verify(exactly = 1) { weightServiceImpl.delete(client, measure) }

    }

}
