package com.coach.flame.testing.integration.base

import com.coach.flame.FlameCoachServiceApplication
import com.coach.flame.customer.security.HashPassword
import com.coach.flame.customer.security.Salt
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.entity.maker.*
import com.coach.flame.jpa.repository.*
import com.coach.flame.jpa.repository.cache.ConfigCache
import com.coach.flame.jpa.repository.configs.CountryConfigRepository
import com.coach.flame.jpa.repository.configs.GenderConfigRepository
import com.google.gson.JsonObject
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    protected lateinit var dailyTaskRepository: DailyTaskRepository

    @Autowired
    protected lateinit var coachRepository: CoachRepository

    @Autowired
    protected lateinit var registrationInviteRepository: RegistrationInviteRepository

    @Autowired
    protected lateinit var countryConfigRepository: CountryConfigRepository

    @Autowired
    protected lateinit var genderConfigRepository: GenderConfigRepository

    @Autowired
    protected lateinit var saltTool: Salt

    @Autowired
    protected lateinit var hashPasswordTool: HashPassword

    @Autowired
    protected lateinit var countryConfigCache: ConfigCache<CountryConfig>

    @Autowired
    protected lateinit var customerTypeCache: ConfigCache<ClientType>

    @Autowired
    protected lateinit var genderConfigCache: ConfigCache<GenderConfig>

    protected val userMaker: Maker<User> = an(UserMaker.User)
    protected val coachMaker: Maker<Coach> = an(CoachMaker.Coach)
    protected val userSessionMaker: Maker<UserSession> = an(UserSessionMaker.UserSession)

    protected var enableDatabaseClean: Boolean = true

    /**
     * Use this request for integration tests
     */
    var request: RequestEntity<String>? = null

    protected lateinit var clientType: ClientType
    protected lateinit var coachType: ClientType

    @BeforeEach
    fun setup() {
        // FIXME: Please do not change this order. Check [ClientType.toClientType]
        coachType = clientTypeRepository.saveAndFlush(
            ClientTypeBuilder.maker()
                .but(MakeItEasy.with(ClientTypeMaker.type, "COACH"))
                .make()
        )
        clientType = clientTypeRepository.saveAndFlush(
            ClientTypeBuilder.maker()
                .but(MakeItEasy.with(ClientTypeMaker.type, "CLIENT"))
                .make()
        )
    }

    @AfterEach
    fun cleanUp() {
        if (enableDatabaseClean) {
            LOGGER.info("opr='cleanUp', msg='Cleaning database...this process will truncate all tables'")
            sqlClean.beforeEach()
        }

        LOGGER.info("opr='cleanUp', msg='Cleaning caches...'")
        countryConfigCache.clean()
        customerTypeCache.clean()
        genderConfigCache.clean()

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
