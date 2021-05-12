package com.coach.flame.testing.component.base

import com.coach.flame.FlameCoachServiceApplication
import com.coach.flame.customer.security.HashPassword
import com.coach.flame.customer.security.Salt
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.entity.maker.*
import com.coach.flame.jpa.repository.*
import com.coach.flame.jpa.repository.cache.ConfigCache
import com.google.gson.JsonObject
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import io.mockk.clearAllMocks
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
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
    classes = [
        ComponentTestConfig::class,
        MockClientRepository::class
    ]
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

    @Autowired
    protected lateinit var clientTypeRepositoryMock: ClientTypeRepository

    @Deprecated("Please use MockClientRepository.kt")
    @Autowired
    protected lateinit var clientRepositoryMock: ClientRepository

    @Autowired
    protected lateinit var mockClientRepository: MockClientRepository

    @Autowired
    protected lateinit var userRepositoryMock: UserRepository

    @Autowired
    protected lateinit var userSessionRepositoryMock: UserSessionRepository

    @Autowired
    protected lateinit var dailyTaskRepositoryMock: DailyTaskRepository

    @Autowired
    protected lateinit var coachRepositoryMock: CoachRepository

    @Autowired
    protected lateinit var countryConfigCacheMock: ConfigCache<CountryConfig>

    @Autowired
    protected lateinit var genderConfigCacheMock: ConfigCache<GenderConfig>

    @Autowired
    protected lateinit var saltTool: Salt

    @Autowired
    protected lateinit var hashPasswordTool: HashPassword

    protected lateinit var clientMaker: Maker<Client>
    protected lateinit var coachMaker: Maker<Coach>
    protected lateinit var clientTypeMaker: Maker<ClientType>
    protected lateinit var userMaker: Maker<User>
    protected lateinit var userSessionMaker: Maker<UserSession>
    protected lateinit var dailyTaskMaker: Maker<DailyTask>

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @BeforeEach
    fun setup() {
        clientMaker = an(ClientMaker.Client)
        coachMaker = an(CoachMaker.Coach)
        clientTypeMaker = an(ClientTypeMaker.ClientType)
        userMaker = an(UserMaker.User)
        userSessionMaker = an(UserSessionMaker.UserSession)
        dailyTaskMaker = an(DailyTaskMaker.DailyTask)
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

    protected fun thenErrorCode(body: JsonObject): AbstractStringAssert<*> {
        val param = body.getAsJsonPrimitive("code").asString
        requireNotNull(param) { "body doesn't have any code parameter" }
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
