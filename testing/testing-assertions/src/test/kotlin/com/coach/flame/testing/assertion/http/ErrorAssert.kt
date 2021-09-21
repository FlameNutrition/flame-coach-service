package com.coach.flame.testing.assertion.http

import com.google.gson.JsonObject
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractObjectAssert
import java.util.*

class ErrorAssert(appointmentResponse: JsonObject) :
    AbstractObjectAssert<ErrorAssert, JsonObject>(
        appointmentResponse, ErrorAssert::class.java
    ) {

    companion object {
        fun assertThat(appointmentResponse: JsonObject): ErrorAssert {
            return ErrorAssert(appointmentResponse)
        }
    }

    fun hasErrorMessageTypeEndsWith(type: String): ErrorAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting error type to be:\n  <%s>\nbut was:\n  <%s>"

        val param = actual.getAsJsonPrimitive("type").asString

        if (!param.endsWith(type)) {
            failWithMessage(assertjErrorMessage, type, param)
        }

        return this
    }

    fun hasErrorMessageTitle(title: String): ErrorAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting error title to be:\n  <%s>\nbut was:\n  <%s>"

        val param = actual.getAsJsonPrimitive("title").asString

        if (!Objects.equals(param, title)) {
            failWithMessage(assertjErrorMessage, title, param)
        }

        return this
    }

    fun hasErrorMessageDetail(detail: String): ErrorAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting error detail to be:\n  <%s>\nbut was:\n  <%s>"

        val param = actual.getAsJsonPrimitive("detail").asString

        if (!Objects.equals(param, detail)) {
            failWithMessage(assertjErrorMessage, detail, param)
        }

        return this
    }

    fun hasErrorMessageStatus(status: String): ErrorAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting error status to be:\n  <%s>\nbut was:\n  <%s>"

        val param = actual.getAsJsonPrimitive("status").asString

        if (!Objects.equals(param, status)) {
            failWithMessage(assertjErrorMessage, status, param)
        }

        return this
    }

    fun hasErrorMessageCode(code: String): ErrorAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting error code to be:\n  <%s>\nbut was:\n  <%s>"

        val param = actual.getAsJsonPrimitive("code").asString

        if (!Objects.equals(param, code)) {
            failWithMessage(assertjErrorMessage, code, param)
        }

        return this
    }

    fun hasErrorMessageInstance(): ErrorAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting error instance not be empty"

        val param = actual.getAsJsonPrimitive("instance").asString

        if (param.isEmpty()) {
            failWithMessage(assertjErrorMessage)
        }

        return this
    }

    fun notHasErrorMessageDebug(): ErrorAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting error debug be empty"

        val param = actual.getAsJsonPrimitive("debug").asString

        if (param.isNotEmpty()) {
            failWithMessage(assertjErrorMessage)
        }

        return this
    }


}
