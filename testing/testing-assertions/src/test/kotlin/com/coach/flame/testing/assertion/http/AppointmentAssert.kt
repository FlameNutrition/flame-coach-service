package com.coach.flame.testing.assertion.http

import com.google.gson.JsonObject
import org.assertj.core.api.AbstractAssert
import java.util.*

class AppointmentAssert(appointmentResponse: JsonObject) :
    AbstractAssert<AppointmentAssert, JsonObject>(
        appointmentResponse, AppointmentAssert::class.java) {

    companion object {
        fun assertThat(appointmentResponse: JsonObject): AppointmentAssert {
            return AppointmentAssert(appointmentResponse)
        }
    }

    fun hasSize(size: Int): AppointmentAssert {

        isNotNull
        val assertjErrorMessage = "\nExpecting size of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val actualSize = actual.getAsJsonArray("appointments").size()
        if (size != actualSize) {
            failWithMessage(assertjErrorMessage, actual, size, actualSize)
        }

        return this
    }

    fun hasIdentifier(uuid: UUID): AppointmentAssert {

        isNotNull
        val assertjErrorMessage = "\nExpecting identifier:\n  <%s>\nto be present at\n  <%s>"

        if (actual.getAsJsonArray("appointments")
                .firstOrNull {
                    it.asJsonObject.getAsJsonPrimitive("identifier").asString == uuid.toString()
                } == null
        ) {
            failWithMessage(assertjErrorMessage, uuid.toString(), actual)
        }

        return this
    }

    fun hasDttmStarts(uuid: UUID, dttmStarts: String): AppointmentAssert {

        isNotNull
        val assertjErrorMessage = "\nExpecting dttmStart of identifier:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val appointment = actual.getAsJsonArray("appointments").first {
            it.asJsonObject.getAsJsonPrimitive("identifier").asString == uuid.toString()
        }

        val actualDttmStarts = appointment.asJsonObject.getAsJsonPrimitive("dttmStarts").asString

        if (!Objects.equals(actualDttmStarts, dttmStarts)) {
            failWithMessage(assertjErrorMessage, uuid.toString(), dttmStarts, actualDttmStarts)
        }

        return this
    }

    fun hasDttmEnds(uuid: UUID, dttmEnds: String): AppointmentAssert {

        isNotNull
        val assertjErrorMessage = "\nExpecting dttmEnds of identifier:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val appointment = actual.getAsJsonArray("appointments").first {
            it.asJsonObject.getAsJsonPrimitive("identifier").asString == uuid.toString()
        }

        val actualDttmEnds = appointment.asJsonObject.getAsJsonPrimitive("dttmEnds").asString

        if (!Objects.equals(actualDttmEnds, dttmEnds)) {
            failWithMessage(assertjErrorMessage, uuid.toString(), dttmEnds, actualDttmEnds)
        }

        return this
    }

    fun hasPrice(uuid: UUID, price: Float): AppointmentAssert {

        isNotNull
        val assertjErrorMessage = "\nExpecting price of identifier:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val appointment = actual.getAsJsonArray("appointments").first {
            it.asJsonObject.getAsJsonPrimitive("identifier").asString == uuid.toString()
        }

        val actualPrice = appointment.asJsonObject.getAsJsonPrimitive("price").asFloat

        if (!Objects.equals(actualPrice, price)) {
            failWithMessage(assertjErrorMessage, uuid.toString(), price, actualPrice)
        }

        return this
    }

    fun hasNotes(uuid: UUID, notes: String): AppointmentAssert {

        isNotNull
        val assertjErrorMessage = "\nExpecting notes of identifier:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val appointment = actual.getAsJsonArray("appointments").first {
            it.asJsonObject.getAsJsonPrimitive("identifier").asString == uuid.toString()
        }

        val actualNotes = appointment.asJsonObject.getAsJsonPrimitive("notes").asString

        if (!Objects.equals(actualNotes, notes)) {
            failWithMessage(assertjErrorMessage, uuid.toString(), notes, actualNotes)
        }

        return this
    }

    fun hasClient(uuid: UUID, clientIdentifier: UUID): AppointmentAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting client->identifier of identifier:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val appointment = actual.getAsJsonArray("appointments").first {
            it.asJsonObject.getAsJsonPrimitive("identifier").asString == uuid.toString()
        }

        val actualClientIdentifier =
            appointment.asJsonObject.getAsJsonObject("client").getAsJsonPrimitive("identifier").asString

        if (!Objects.equals(actualClientIdentifier, clientIdentifier.toString())) {
            failWithMessage(assertjErrorMessage, uuid.toString(), clientIdentifier, actualClientIdentifier)
        }

        return this
    }

    fun hasClientFirstname(uuid: UUID, firstName: String): AppointmentAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting client->firstName of identifier:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val appointment = actual.getAsJsonArray("appointments").first {
            it.asJsonObject.getAsJsonPrimitive("identifier").asString == uuid.toString()
        }

        val actualClientFirstName =
            appointment.asJsonObject.getAsJsonObject("client").getAsJsonPrimitive("firstName").asString

        if (!Objects.equals(actualClientFirstName, firstName)) {
            failWithMessage(assertjErrorMessage, uuid.toString(), firstName, actualClientFirstName)
        }

        return this
    }

    fun hasClientLastname(uuid: UUID, lastName: String): AppointmentAssert {

        isNotNull
        val assertjErrorMessage =
            "\nExpecting client->lastName of identifier:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>"

        val appointment = actual.getAsJsonArray("appointments").first {
            it.asJsonObject.getAsJsonPrimitive("identifier").asString == uuid.toString()
        }

        val actualClientLastName =
            appointment.asJsonObject.getAsJsonObject("client").getAsJsonPrimitive("lastName").asString

        if (!Objects.equals(actualClientLastName, lastName)) {
            failWithMessage(assertjErrorMessage, uuid.toString(), lastName, actualClientLastName)
        }

        return this
    }

}
