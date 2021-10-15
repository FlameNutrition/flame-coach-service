package com.coach.flame.api.calculator

import com.coach.flame.api.calculator.request.CalculatorRequest
import com.coach.flame.api.calculator.response.CalculatorResponse
import com.coach.flame.calculators.CalculatorService

/**
 * Use this interface if you need to implement a new calculator in the API.
 *
 * @author Nuno Bento
 */
interface CalculatorAPI {

    /**
     * Get the result of your calculation. This uses the [CalculatorResponse] to retrieve it.
     *
     * @param request with the necessary information to do the maths
     *
     * @return [CalculatorResponse] response with result and units
     */
    fun calculate(request: CalculatorRequest): CalculatorResponse
}
