package com.coach.flame.testing.component.base

import com.coach.flame.FlameCoachServiceApplication
import com.coach.flame.jpa.entity.*
import com.google.gson.JsonObject
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.web.servlet.RequestBuilder

@SpringBootTest(
    classes = [FlameCoachServiceApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ContextConfiguration(
    classes = [ComponentTestConfig::class]
)
@TestExecutionListeners(
    value = [
        LoadRequestExecutionListener::class
    ],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
abstract class BaseComponentTest {

    var request: RequestBuilder? = null

    protected lateinit var clientMaker: Maker<Client>
    protected lateinit var clientTypeMaker: Maker<ClientType>
    protected lateinit var userMaker: Maker<User>

    @BeforeEach
    fun setup() {
        clientMaker = an(ClientMaker.Client)
        clientTypeMaker = an(ClientTypeMaker.ClientType)
        userMaker = an(UserMaker.User)
    }

    protected fun thenErrorMessageType(body: JsonObject): AbstractStringAssert<*> {
        val param = body.getAsJsonPrimitive("type").asString
        requireNotNull(param) { "body doesn't have any type parameter" }
        return assertThat(param)
    }

    protected fun thenErrorMessageTitle(body: JsonObject): AbstractStringAssert<*> {
        val param = body.getAsJsonPrimitive("title").asString
        requireNotNull(param) { "body doesn't have any title parameter" }
        return assertThat(param)
    }

    protected fun thenErrorMessageDetail(body: JsonObject): AbstractStringAssert<*> {
        val param = body.getAsJsonPrimitive("detail").asString
        requireNotNull(param) { "body doesn't have any detail parameter" }
        return assertThat(param)
    }

    protected fun thenErrorMessageStatus(body: JsonObject): AbstractStringAssert<*> {
        val param = body.getAsJsonPrimitive("status").asString
        requireNotNull(param) { "body doesn't have any status parameter" }
        return assertThat(param)
    }

    protected fun thenErrorMessageInstance(body: JsonObject): AbstractStringAssert<*> {
        val param = body.getAsJsonPrimitive("instance").asString
        requireNotNull(param) { "body doesn't have any instance parameter" }
        return assertThat(param)
    }

    protected fun thenErrorMessageDebug(body: JsonObject): AbstractStringAssert<*> {
        val param = body.getAsJsonPrimitive("debug").asString
        requireNotNull(param) { "body doesn't have any debug parameter" }
        return assertThat(param)
    }

}