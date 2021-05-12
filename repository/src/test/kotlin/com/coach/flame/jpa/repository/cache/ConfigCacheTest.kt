package com.coach.flame.jpa.repository.cache

import com.coach.flame.jpa.entity.ClientType
import com.coach.flame.jpa.entity.maker.ClientTypeBuilder
import com.coach.flame.jpa.entity.maker.ClientTypeMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class ConfigCacheTest {

    @MockK
    private lateinit var repository: CacheLoaderRepository<ClientType>

    private lateinit var classToTest: ConfigCache<ClientType>

    @BeforeEach
    fun setUp() {
        classToTest = ConfigCache(15, TimeUnit.SECONDS, repository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun testGetValueCache() {

        val client = ClientTypeBuilder.default()
        val coach = ClientTypeBuilder.maker()
            .but(with(ClientTypeMaker.type, "COACH"))
            .make()

        every { repository.findByKey("CLIENT") } returns client
        every { repository.findByKey("COACH") } returns coach
        every { repository.findByKey("INVALID") } returns null

        then(classToTest.getValue("CLIENT").get()).isEqualTo(client)
        then(classToTest.getValue("COACH").get()).isEqualTo(coach)
        then(classToTest.getValue("COACH").get()).isEqualTo(coach)
        then(classToTest.getValue("INVALID").isEmpty).isTrue

        verify(exactly = 1) { repository.findByKey("CLIENT") }
        verify(exactly = 1) { repository.findByKey("COACH") }
    }

    @Test
    fun testGetValueExpirationTimeCache() {

        val client = ClientTypeBuilder.default()
        val coach = ClientTypeBuilder.maker()
            .but(with(ClientTypeMaker.type, "COACH"))
            .make()

        every { repository.findByKey("CLIENT") } returns client
        every { repository.findByKey("COACH") } returns coach

        then(classToTest.getValue("CLIENT").get()).isEqualTo(client)
        then(classToTest.getValue("COACH").get()).isEqualTo(coach)
        then(classToTest.getValue("COACH").get()).isEqualTo(coach)

        TimeUnit.SECONDS.sleep(20)

        then(classToTest.getValue("CLIENT").get()).isEqualTo(client)
        then(classToTest.getValue("COACH").get()).isEqualTo(coach)
        verify(exactly = 2) { repository.findByKey("CLIENT") }
        verify(exactly = 2) { repository.findByKey("COACH") }

    }

    @Test
    fun testCleanCache() {

        val client = ClientTypeBuilder.default()

        every { repository.findByKey("CLIENT") } returns client

        then(classToTest.getValue("CLIENT").get()).isEqualTo(client)

        classToTest.clean()

        then(classToTest.getValue("CLIENT").get()).isEqualTo(client)

        verify(exactly = 2) { repository.findByKey("CLIENT") }
    }
}
