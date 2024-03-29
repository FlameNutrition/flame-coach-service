package com.coach.flame.testing.framework

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoadRequest(
    val pathOfRequest: String = "",
    val request: String = "",
    //FIXME: Change this for a ENUM
    val contentType: String = MediaType.APPLICATION_JSON_VALUE,
    val endpoint: String,
    val port: String = "5000",
    val httpMethod: RequestMethod,

    /**
     * Please use the character ":" to set the headers
     * E.g: "<header>:<value>"
     */
    val headers: Array<String> = [],

    /**
     * Please use the character ":" to set the parameters
     * E.g: "<parameter>:<value>"
     */
    val parameters: Array<String> = [],
)