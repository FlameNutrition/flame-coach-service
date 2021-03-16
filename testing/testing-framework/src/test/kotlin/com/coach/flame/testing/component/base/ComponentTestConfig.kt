package com.coach.flame.testing.component.base

import com.coach.flame.jpa.repository.*
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * Use this test configuration to declare beans or overrides
 */
@TestConfiguration
class ComponentTestConfig {

    @Primary
    @Bean(name = ["clientRepositoryMock"])
    fun clientRepository() = mockk<ClientRepository>(relaxed = false)

    @Primary
    @Bean(name = ["userRepositoryMock"])
    fun userRepository() = mockk<UserRepository>(relaxed = false)

    @Primary
    @Bean(name = ["userSessionRepositoryMock"])
    fun userSessionRepository() = mockk<UserSessionRepository>(relaxed = false)

    @Primary
    @Bean(name = ["clientTypeRepositoryMock"])
    fun clientTypeRepository() = mockk<ClientTypeRepository>(relaxed = false)

    @Primary
    @Bean(name = ["dailyTaskRepositoryMock"])
    fun dailyTaskRepository() = mockk<DailyTaskRepository>(relaxed = false)

    @Primary
    @Bean(name = ["coachRepositoryMock"])
    fun coachRepository() = mockk<CoachRepository>(relaxed = false)

}