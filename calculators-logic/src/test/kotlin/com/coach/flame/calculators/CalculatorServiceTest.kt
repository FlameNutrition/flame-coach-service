package com.coach.flame.calculators

import com.coach.flame.domain.FormulaInputDto
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class CalculatorServiceTest {

    @MockK(relaxed = true)
    private lateinit var harrisBenedictFormula: HarrisBenedictFormula

    @MockK(relaxed = true)
    private lateinit var caloriesConverter: CaloriesConverter

    @InjectMockKs
    private lateinit var classToTest: CalculatorService

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test calculator receive invalid formula`() {

        val result = catchThrowable { classToTest.calculate("INVALID", CalculatorService.Unit.KILOCALORIES, mockk()) }

        then(result).isInstanceOf(UnsupportedFormulaException::class.java)
            .hasMessage("INVALID formula is not supported.")

    }

    @Test
    fun `test calculator receive harris benedict formula`() {

        val formulaInput = mockk<FormulaInputDto>()

        val result = classToTest
            .calculate(HarrisBenedictFormula.NAME, CalculatorService.Unit.KILOCALORIES, formulaInput)

        then(result).isNotNull
        verify(exactly = 1) { harrisBenedictFormula.result(formulaInput) }
        verify(exactly = 2) { caloriesConverter.convertKilocalories(any(), CalculatorService.Unit.KILOCALORIES) }

    }

}


