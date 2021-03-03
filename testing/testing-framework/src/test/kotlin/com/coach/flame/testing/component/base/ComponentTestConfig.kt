package com.coach.flame.testing.component.base

import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.ClientTypeRepository
import com.coach.flame.jpa.repository.DailyTaskRepository
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.*

/**
 * Use this test configuration to declare beans or overrides
 */
@TestConfiguration
class ComponentTestConfig {

    @Primary
    @Bean(name = ["clientRepositoryMock"])
    fun clientRepository(): ClientRepository {
        return mockk()
    }

    @Primary
    @Bean(name = ["clientTypeRepositoryMock"])
    fun clientTypeRepository(): ClientTypeRepository {
        return mockk()
    }

    @Primary
    @Bean(name = ["dailyTaskRepositoryMock"])
    fun dailyTaskRepository(): DailyTaskRepository {
        return mockk()
    }

}