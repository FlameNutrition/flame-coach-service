package com.coach.flame.testing.integration.base

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

/**
 * Use this test configuration to declare beans or overrides
 */
@TestConfiguration
class IntegrationTestConfig {

    @Bean
    fun sqlClean(): SQLClean {
        return SQLClean()
    }

}