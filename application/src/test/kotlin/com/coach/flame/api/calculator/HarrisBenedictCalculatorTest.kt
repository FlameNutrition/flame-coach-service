package com.coach.flame.api.calculator

import com.coach.flame.api.calculator.request.CalculatorRequest
import com.coach.flame.calculators.CalculatorService
import com.coach.flame.calculators.HarrisBenedictFormula
import com.coach.flame.domain.CalculatorOutputDto
import com.coach.flame.domain.FormulaInputDto
import com.coach.flame.domain.FormulaInputDto.HeightUnit.CM
import com.coach.flame.domain.FormulaInputDto.Pal.LEVEL_1_LIGHT_EXERCISE
import com.coach.flame.domain.FormulaInputDto.Sex.MALE
import com.coach.flame.domain.FormulaInputDto.WeightUnit.KG
import com.coach.flame.exception.RestInvalidRequestException
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class HarrisBenedictCalculatorTest {

    @MockK
    private lateinit var calculatorService: CalculatorService

    @InjectMockKs
    private lateinit var classToTest: HarrisBenedictCalculator

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test Harris Benedict formula with invalid sex parameter`() {

        val request = CalculatorRequest().apply {
            weight = 70.5
            height = 175.0
            age = 31
            sex = "INVALID"
        }

        val result = catchThrowable { classToTest.calculate(request) }

        verify(exactly = 0) { calculatorService.calculate(any(), any(), any()) }
        then(result).isInstanceOf(RestInvalidRequestException::class.java)

    }

    @Test
    fun `test Harris Benedict formula with invalid unit parameter`() {

        val request = CalculatorRequest().apply {
            unit = "INVALID"
        }

        val result = catchThrowable { classToTest.calculate(request) }

        verify(exactly = 0) { calculatorService.calculate(any(), any(), any()) }
        then(result).isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessage("unit parameter does not have the correct format. e.g: kg/cm.")

    }

    @Test
    fun `test Harris Benedict formula with invalid weight unit parameter`() {

        val request = CalculatorRequest().apply {
            weight = 70.5
            height = 175.0
            age = 31
            sex = "INVALID"
            unit = "invalid/cm"
        }

        val result = catchThrowable { classToTest.calculate(request) }

        verify(exactly = 0) { calculatorService.calculate(any(), any(), any()) }
        then(result).isInstanceOf(RestInvalidRequestException::class.java)
    }

    @Test
    fun `test Harris Benedict formula with invalid height unit parameter`() {

        val request = CalculatorRequest().apply {
            weight = 70.5
            height = 175.0
            age = 31
            sex = "INVALID"
            unit = "kg/invalid"
        }

        val result = catchThrowable { classToTest.calculate(request) }

        verify(exactly = 0) { calculatorService.calculate(any(), any(), any()) }
        then(result).isInstanceOf(RestInvalidRequestException::class.java)
    }

    @Test
    fun `test Harris Benedict formula without pal`() {

        val calculatorResult = CalculatorOutputDto(100.4, 200.5)

        every { calculatorService.calculate(any(), any(), any()) } returns calculatorResult

        val request = CalculatorRequest().apply {
            weight = 70.5
            height = 175.0
            age = 31
            sex = "male"
            unit = "kg/cm"
            caloriesUnit = "kilocalories"
        }

        val result = classToTest.calculate(request)

        verify(exactly = 1) {
            val expected = FormulaInputDto(70.5, 175.0, 31, MALE, KG, CM, null)
            calculatorService.calculate(HarrisBenedictFormula.NAME, CalculatorService.Unit.KILOCALORIES, expected)
        }
        then(result)
            .hasFieldOrPropertyWithValue("result", 100.4)
            .hasFieldOrPropertyWithValue("unit", "KILOCALORIES")
            .hasFieldOrPropertyWithValue("perUnit", "day")

    }

    @Test
    fun `test Harris Benedict formula with pal`() {

        val calculatorResult = CalculatorOutputDto(100.4, 200.5)

        every { calculatorService.calculate(any(), any(), any()) } returns calculatorResult

        val request = CalculatorRequest().apply {
            weight = 70.5
            height = 175.0
            age = 31
            sex = "male"
            unit = "kg/cm"
            pal = 1
            caloriesUnit = "calories"
        }

        val result = classToTest.calculate(request)

        verify(exactly = 1) {
            val expected = FormulaInputDto(70.5, 175.0, 31, MALE, KG, CM, LEVEL_1_LIGHT_EXERCISE)
            calculatorService.calculate(HarrisBenedictFormula.NAME, CalculatorService.Unit.CALORIES, expected)
        }
        then(result)
            .hasFieldOrPropertyWithValue("result", 200.5)
            .hasFieldOrPropertyWithValue("unit", "CALORIES")
            .hasFieldOrPropertyWithValue("perUnit", "day")

    }

}
