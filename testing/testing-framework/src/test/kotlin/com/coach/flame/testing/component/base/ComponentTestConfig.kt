package com.coach.flame.testing.component.base

import com.coach.flame.jpa.entity.CountryConfig
import com.coach.flame.jpa.entity.GenderConfig
import com.coach.flame.jpa.repository.*
import com.coach.flame.jpa.repository.cache.ConfigCache
import com.coach.flame.jpa.repository.operations.ClientRepositoryOperation
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.mail.javamail.JavaMailSender

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

    @Primary
    @Bean(name = ["coachOperationsMock"])
    fun coachOperations() = mockk<CoachRepositoryOperation>(relaxed = false)

    @Primary
    @Bean(name = ["clientOperationsMock"])
    fun clientOperations() = mockk<ClientRepositoryOperation>(relaxed = false)

    @Primary
    @Bean(name = ["registrationInviteRepositoryMock"])
    fun registrationInviteRepository() = mockk<RegistrationInviteRepository>(relaxed = false)

    @Primary
    @Bean(name = ["countryConfigCacheMock"])
    fun countryConfigCache() = mockk<ConfigCache<CountryConfig>>(relaxed = false)

    @Primary
    @Bean(name = ["genderConfigCacheMock"])
    fun genderConfigCache() = mockk<ConfigCache<GenderConfig>>(relaxed = false)

    @Primary
    @Bean(name = ["appointmentRepositoryMock"])
    fun appointmentRepository() = mockk<AppointmentRepository>(relaxed = false)

    @Primary
    @Bean(name = ["emailSender"])
    fun emailSender() = mockk<JavaMailSender>(relaxed = false)

}
