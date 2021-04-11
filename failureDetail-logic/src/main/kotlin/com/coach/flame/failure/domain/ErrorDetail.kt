package com.coach.flame.failure.domain

import com.coach.flame.failure.Status
import com.coach.flame.failure.exception.BusinessException
import com.fasterxml.jackson.annotation.JsonInclude
import java.net.URI
import java.util.*
import java.util.Objects.hash

class ErrorDetail private constructor(
    val type: URI?,
    val code: Int?,
    val title: String?,
    val detail: String?,
    val status: Int,
    val instance: URI?,
    val debug: String = "",
) {

    /**
     * Thanks (Credits)
     * https://blog.codecentric.de/en/2020/01/rfc-7807-problem-details-with-spring-boot-and-jax-rs/
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Builder(
        private var type: URI? = null,
        private var errorCode: ErrorCode = ErrorCode.CODE_9999,
        private var title: String? = null,
        private var detail: String? = null,
        private var status: Int = 500,
        private var instance: URI? = null,
        private var debug: String = "",
        private var throwable: Throwable? = null,
        private var enableDebug: Boolean = false,
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

        private fun buildCode() {
            this.errorCode = if (throwable is BusinessException) {
                (throwable as BusinessException).errorCode
            } else {
                ErrorCode.CODE_9999
            }
        }

        fun withEnableDebug(enableDebug: Boolean) = apply {
            this.enableDebug = enableDebug
        }

        fun throwable(throwable: Throwable) = apply {
            this.throwable = throwable
            buildType()
            buildTitle()
            if (enableDebug) {
                buildDebug()
            }
            buildStatus()
            buildInstance()
            buildDetail()
            buildCode()
        }

        fun build() = ErrorDetail(type, errorCode.code, title, detail, status, instance, debug)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ErrorDetail

        if (instance != other.instance) return false

        return true
    }

    override fun hashCode(): Int {
        return hash(instance)
    }

}

