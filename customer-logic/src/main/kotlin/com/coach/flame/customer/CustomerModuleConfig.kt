package com.coach.flame.customer

import com.coach.flame.FlameCoachRepoConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(value = [
    "com.coach.flame.customer",
    "com.coach.flame.customer.coach",
    "com.coach.flame.domain.converters"])
@Import(value = [FlameCoachRepoConfig::class])
class CustomerModuleConfig