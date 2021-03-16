package com.coach.flame.testing.component.base

import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.google.gson.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestMethod
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

class LoadRequestExecutionListener : TestExecutionListener {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(LoadRequestExecutionListener::class.java)
    }

    override fun beforeTestMethod(testContext: TestContext) {

        val loadRequestAnnotation = testContext.testMethod.annotations
            .filterIsInstance<LoadRequest>()
            .firstOrNull()

        requireNotNull(loadRequestAnnotation) { "please apply a @LoadRequest annotation in your test" }

        val headers = HttpHeaders()
        loadRequestAnnotation.headers.forEach {
            val pair = it.split(":")
            val header = pair[0]
            val value = pair[1]

            headers.set(header, value)
        }

        val request: RequestBuilder = when (loadRequestAnnotation.httpMethod) {
            RequestMethod.POST -> {

                val json: JsonObject = if (loadRequestAnnotation.pathOfRequest.isEmpty()) {
                    LOGGER.info("opr='beforeTestMethod', 'Loading test using request'")
                    JsonBuilder.getJsonFromString(loadRequestAnnotation.request)
                } else {
                    LOGGER.info("opr='beforeTestMethod', 'Loading test using request file'")
                    JsonBuilder.getJsonFromFile(loadRequestAnnotation.pathOfRequest)
                }

                MockMvcRequestBuilders.post(loadRequestAnnotation.endpoint)
                    .headers(headers)
                    .contentType(MediaType.valueOf(loadRequestAnnotation.contentType))
                    .content(json.toString())
            }
            RequestMethod.GET -> {

                val parameters = LinkedMultiValueMap<String, String>()
                loadRequestAnnotation.parameters.forEach {
                    val pair = it.split(":")
                    val param = pair[0]
                    val value = pair[1]

                    parameters.set(param, value)
                }

                MockMvcRequestBuilders.get(loadRequestAnnotation.endpoint)
                    .headers(headers)
                    .params(parameters)
            }
            else -> MockMvcRequestBuilders.get("/")
        }

        BaseComponentTest::class.memberProperties.filter { it.name == "request" }
            .filterIsInstance<KMutableProperty<*>>()
            .first()
            .setter
            .call(testContext.testInstance, request)

        LOGGER.info("opr='beforeTestMethod', 'Loaded request with: {}'", request)

    }
}