package com.coach.flame.api.calculator

import com.coach.flame.api.APIWrapperException
import com.coach.flame.api.calculator.request.CalculatorRequest
import com.coach.flame.api.calculator.response.CalculatorResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.calculators.CalculatorService
import com.coach.flame.calculators.HarrisBenedictFormula
import com.coach.flame.domain.FormulaInputDto
import com.coach.flame.exception.RestInvalidRequestException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/calculator")
class HarrisBenedictCalculator(
    private val calculatorService: CalculatorService
) : CalculatorAPI {

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/harrisBenedict")
    @ResponseBody
    override fun calculate(
        @Valid request: CalculatorRequest
    ): CalculatorResponse {
        return APIWrapperException.executeRequest {

            val splitUnit = request.unit.split("/")

            if (splitUnit.size != 2) {
                throw RestInvalidRequestException("unit parameter does not have the correct format. e.g: kg/cm.")
            }

            val formulaInput = FormulaInputDto(
                weight = request.weight!!,
                height = request.height!!,
                age = request.age!!,
                sex = FormulaInputDto.Sex.valueOf(request.sex!!.toUpperCase()),
                weightUnit = FormulaInputDto.WeightUnit.valueOf(splitUnit[0].toUpperCase()),
                heightUnit = FormulaInputDto.HeightUnit.valueOf(splitUnit[1].toUpperCase()),
                pal = request.pal?.let { FormulaInputDto.Pal.getByLevel(it) }
            )

            val caloriesUnit = CalculatorService.Unit.valueOf(request.caloriesUnit.toUpperCase())

            val resultCalculator = calculatorService.calculate(HarrisBenedictFormula.NAME, caloriesUnit, formulaInput)

            var calories = resultCalculator.bmr
            request.pal?.let {
                calories = resultCalculator.tdee
            }

            CalculatorResponse(calories, caloriesUnit.name)
        }
    }
}
