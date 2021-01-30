package com.coach.flame.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.net.URI

class ErrorDetail private constructor(
    val type: URI?,
    val title: String?,
    val detail: String?,
    val status: Int?,
    val instance: URI?,
    val debug: String?
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Builder(
        var type: URI? = null,
        var title: String? = null,
        var detail: String? = null,
        var status: Int? = null,
        var instance: URI? = null,
        var debug: String? = null
    ) {
        fun type(type: URI) = apply { this.type = type }
        fun title(title: String) = apply { this.title = title }
        fun detail(detail: String) = apply { this.detail = detail }
        fun status(status: Int) = apply { this.status = status }
        fun instance(instance: URI) = apply { this.instance = instance }
        fun debug(debug: String) = apply { this.debug = debug }
        fun build() = ErrorDetail(type, title, detail, status, instance, debug)
    }

}

