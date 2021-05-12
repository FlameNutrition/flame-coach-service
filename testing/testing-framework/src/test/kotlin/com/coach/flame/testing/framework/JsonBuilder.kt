package com.coach.flame.testing.framework

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mock.web.MockHttpServletResponse
import java.nio.charset.Charset

object JsonBuilder {

    private val LOGGER: Logger = LoggerFactory.getLogger(JsonBuilder::class.java)

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    /**
     * Function get data json from a file inside the classpath then convert it to
     * a JsonObject
     *
     * @param fileName file name used
     * @return json data
     */
    fun getJsonFromFile(fileName: String): JsonObject {

        val data = requireNotNull(
            this::class.java.classLoader
                .getResource(fileName)
        ).readText(Charset.defaultCharset())

        if (data.isEmpty()) {
            throw IllegalArgumentException("Data from $fileName wasn't load")
        }

        val jsonObj = gson.fromJson(data, JsonObject::class.java)

        LOGGER.debug("getJsonFromFile: {}", jsonObj)

        return jsonObj

    }

    /**
     * Function get data from a String then convert it to a JsonObject
     *
     * @param string json
     * @return json data
     */
    fun getJsonFromString(string: String): JsonObject {

        val jsonObj = gson.fromJson(string, JsonObject::class.java)

        LOGGER.debug("getJsonFromString: {}", jsonObj)

        return jsonObj
    }

    /**
     * Function get data from [MockHttpServletResponse] then convert it to a JsonObject
     *
     * @param mockClient mock client
     * @return json data
     */
    fun getJsonFromMockClient(mockClient: MockHttpServletResponse): JsonObject {

        val jsonObj = gson.fromJson(mockClient.getContentAsString(Charset.defaultCharset()), JsonObject::class.java)

        LOGGER.debug("getJsonFromString: {}", jsonObj)

        return jsonObj
    }

}
