package com.coach.flame.testing.integration.base

import com.coach.flame.FlameCoachServiceApplication
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.*
import com.google.gson.JsonObject
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.RequestEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners

@SpringBootTest(
    classes = [FlameCoachServiceApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@AutoConfigureTestDatabase
@ContextConfiguration(
    classes = [IntegrationTestConfig::class]
)
@TestExecutionListeners(
    value = [
        LoadRequestExecutionListener::class
    ],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
abstract class BaseIntegrationTest {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BaseIntegrationTest::class.java)
    }

    @Autowired
    private lateinit var sqlClean: SQLClean

    @Autowired
    protected lateinit var clientTypeRepository: ClientTypeRepository

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var clientRepository: ClientRepository

    @Autowired
    protected lateinit var userSessionRepository: UserSessionRepository

    @Autowired
    protected lateinit var coachRepository: CoachRepository

    protected val userMaker: Maker<User> = an(UserMaker.User)
    protected val clientTypeMaker: Maker<ClientType> = an(ClientTypeMaker.ClientType)
    protected val clientMaker: Maker<Client> = an(ClientMaker.Client)
    protected val coachMaker: Maker<Coach> = an(CoachMaker.Coach)
    protected val userSessionMaker: Maker<UserSession> = an(UserSessionMaker.UserSession)

    /**
     * Use this request for integration tests
     */
    var request: RequestEntity<String>? = null

    @BeforeEach
    fun beforeEach() {
        LOGGER.info("opr='beforeEach', msg='Cleaning database...this process will truncate all tables'")
        sqlClean.beforeEach()
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