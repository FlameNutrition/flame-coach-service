package com.coach.flame.testing.integration.base

import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.google.gson.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import org.springframework.web.bind.annotation.RequestMethod
import java.net.URL
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

        val request: RequestEntity<*> = when (loadRequestAnnotation.httpMethod) {
            RequestMethod.POST -> {

                val json: JsonObject = if (loadRequestAnnotation.pathOfRequest.isEmpty()) {
                    LOGGER.info("opr='beforeTestMethod', 'Loading test using request'")
                    JsonBuilder.getJsonFromString(loadRequestAnnotation.request)
                } else {
                    LOGGER.info("opr='beforeTestMethod', 'Loading test using request file'")
                    JsonBuilder.getJsonFromFile(loadRequestAnnotation.pathOfRequest)
                }

                RequestEntity.post(URL("http://localhost:${loadRequestAnnotation.port}/${loadRequestAnnotation.endpoint}").toURI())
                    .contentType(MediaType.valueOf(loadRequestAnnotation.contentType))
                    .body(json.toString())
            }
            else -> RequestEntity.get(URL("http://localhost:${loadRequestAnnotation.port}").toURI()).build()
        }

        BaseIntegrationTest::class.memberProperties.filter { it.name == "request" }
            .filterIsInstance<KMutableProperty<*>>()
            .first()
            .setter
            .call(testContext.testInstance, request)

        LOGGER.info("opr='beforeTestMethod', 'Loaded request with: {}'", request)

    }
}