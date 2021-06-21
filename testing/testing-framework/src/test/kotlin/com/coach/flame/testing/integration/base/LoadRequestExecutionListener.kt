package com.coach.flame.testing.integration.base

import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.google.gson.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
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

        //Add authentication default header
        headers.set(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")

        loadRequestAnnotation.headers.forEach {
            val pair = it.split(":")
            val header = pair[0]
            val value = pair[1]

            headers.set(header, value)
        }

        val builder = UriComponentsBuilder
            .fromHttpUrl("http://localhost:${loadRequestAnnotation.port}/${loadRequestAnnotation.endpoint}")

        loadRequestAnnotation.parameters.forEach {
            val pair = it.split(":")
            val param = pair[0]
            val value = pair[1]

            builder.queryParam(param, value)
        }


        val request: RequestEntity<*> = when (loadRequestAnnotation.httpMethod) {
            RequestMethod.POST -> {

                val json: JsonObject = if (loadRequestAnnotation.pathOfRequest.isEmpty()) {
                    LOGGER.info("opr='beforeTestMethod', 'Loading test using request'")
                    JsonBuilder.getJsonFromString(loadRequestAnnotation.request)
                } else {
                    LOGGER.info("opr='beforeTestMethod', 'Loading test using request file'")
                    JsonBuilder.getJsonFromFile(loadRequestAnnotation.pathOfRequest)
                }

                RequestEntity.post(URI.create(builder.toUriString()))
                    .headers(headers)
                    .contentType(MediaType.valueOf(loadRequestAnnotation.contentType))
                    .body(json.toString())
            }
            RequestMethod.GET -> {
                RequestEntity.get(URI.create(builder.toUriString()))
                    .headers(headers)
                    .build()
            }
            RequestMethod.DELETE -> {
                RequestEntity.delete(URI.create(builder.toUriString()))
                    .headers(headers)
                    .build()
            }
            else -> RequestEntity.get("http://localhost:${loadRequestAnnotation.port}").build()
        }

        BaseIntegrationTest::class.memberProperties.filter { it.name == "request" }
            .filterIsInstance<KMutableProperty<*>>()
            .first()
            .setter
            .call(testContext.testInstance, request)

        LOGGER.info("opr='beforeTestMethod', 'Loaded request with: {}'", request)

    }
}
