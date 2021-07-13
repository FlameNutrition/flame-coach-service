package com.coach.flame

import com.coach.flame.aspect.LoggingServiceConfig
import com.coach.flame.configs.ConfigModuleConfig
import com.coach.flame.customer.CustomerModuleConfig
import com.coach.flame.customer.measures.MeasureModuleConfig
import com.coach.flame.dailyTask.DailyTaskModuleConfig
import com.coach.flame.metrics.MetricsModuleConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication(
    scanBasePackages = [
        "com.coach.flame.api",
        "com.coach.flame.aspect",
        "com.coach.flame.exception"
    ]
)
@Import(
    value = [
        ConfigModuleConfig::class,
        CustomerModuleConfig::class,
        DailyTaskModuleConfig::class,
        LoggingServiceConfig::class,
        MeasureModuleConfig::class,
        MetricsModuleConfig::class,
        FlameCoachWebConfig::class]
)
class FlameCoachServiceApplication
