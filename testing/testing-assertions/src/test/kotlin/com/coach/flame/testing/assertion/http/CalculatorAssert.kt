package com.coach.flame.testing.assertion.http

import com.google.gson.JsonObject
import org.assertj.core.api.AbstractAssert
import java.util.*

class CalculatorAssert(calculatorResponse: JsonObject) :
    AbstractAssert<CalculatorAssert, JsonObject>(
        calculatorResponse, CalculatorAssert::class.java
    ) {

    companion object {
        fun assertThat(calculatorResponse: JsonObject): CalculatorAssert {
            return CalculatorAssert(calculatorResponse)
        }
    }

    fun hasResult(value: Double): CalculatorAssert {

        isNotNull
        val assertjErrorMessage = "\nExpecting result:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val actualValue = actual.getAsJsonPrimitive("result").asDouble

        if (!Objects.equals(actualValue, value)) {
            failWithActualExpectedAndMessage(actualValue, value, assertjErrorMessage, actualValue, value, actualValue)
        }

        return this
    }

    fun hasUnit(unit: String): CalculatorAssert {

        isNotNull
        val assertjErrorMessage = "\nExpecting unit:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val actualValue = actual.getAsJsonPrimitive("unit").asString

        if (!Objects.equals(actualValue, unit)) {
            failWithActualExpectedAndMessage(actualValue, unit, assertjErrorMessage, actualValue, unit, actualValue)
        }

        return this
    }

    fun hasPerUnit(perUnit: String): CalculatorAssert {

        isNotNull
        val assertjErrorMessage = "\nExpecting perUnit:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val actualValue = actual.getAsJsonPrimitive("perUnit").asString

        if (!Objects.equals(actualValue, perUnit)) {
            failWithActualExpectedAndMessage(
                actualValue,
                perUnit,
                assertjErrorMessage,
                actualValue,
                perUnit,
                actualValue
            )
        }

        return this
    }

}
