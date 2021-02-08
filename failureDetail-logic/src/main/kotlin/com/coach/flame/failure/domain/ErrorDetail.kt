package com.coach.flame.failure.domain

import com.coach.flame.failure.Status
import com.fasterxml.jackson.annotation.JsonInclude
import org.apache.logging.log4j.util.Strings
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URI
import java.util.*

class ErrorDetail private constructor(
    val type: URI?,
    val title: String?,
    val detail: String?,
    val status: Int,
    val instance: URI?,
    val debug: String?
) {

    /**
     * Thanks (Credits)
     * https://blog.codecentric.de/en/2020/01/rfc-7807-problem-details-with-spring-boot-and-jax-rs/
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Builder(
        var type: URI? = null,
        var title: String? = null,
        var detail: String? = null,
        var status: Int = 500,
        var instance: URI? = null,
        var debug: String? = null,
        var throwable: Throwable? = null
    ) {

        private fun buildType() {
            val javadocName = throwable!!.javaClass.name
                .replace('.', '/') // the package names are delimited like a path
                .replace('$', '.') // nested classes are delimited with a period

            this.type = URI.create("https://flame-coach/apidocs/$javadocName.html")
        }

        private fun buildTitle() {
            this.title = throwable!!.javaClass.simpleName
        }

        private fun buildDetail() {
            this.detail = throwable!!.message
        }

        private fun buildStatus() {

            try {
                val statusAnnotation: Status = throwable!!.javaClass.getAnnotation(Status::class.java)
                this.status = statusAnnotation.httpStatus.value
            } catch (ex: NullPointerException) {
                this.status = 500
            }

        }

        private fun buildInstance() {
            this.instance = URI.create("urn:uuid:${UUID.randomUUID()}")
        }

        private fun buildDebug() {
            //FIXME: This should be changed
            this.debug = throwable!!.stackTraceToString()
        }

        fun throwable(throwable: Throwable) = apply {
            this.throwable = throwable
            buildType()
            buildTitle()
            buildDetail()
            buildStatus()
            buildInstance()
            buildDebug()
        }

        fun build() = ErrorDetail(type, title, detail, status, instance, debug)
    }

}

