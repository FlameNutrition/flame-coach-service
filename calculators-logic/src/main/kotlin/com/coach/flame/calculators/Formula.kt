package com.coach.flame.calculators

import com.coach.flame.domain.FormulaInputDto
import com.coach.flame.domain.FormulaOutputDto

/**
 * Use this interface if you need to implement a new formula for [CalculatorService].
 *
 * @author Nuno Bento
 */
interface Formula {

    /**
     * This method is going to return the result of the Formula
     *
     * @param formulaInput - Dto object which contains the necessary info to do the maths
     * @return [FormulaOutputDto] with the necessary information to expose the result
     */
    fun result(formulaInput: FormulaInputDto): FormulaOutputDto

}
