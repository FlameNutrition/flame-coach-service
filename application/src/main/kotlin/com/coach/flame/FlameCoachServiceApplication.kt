package com.coach.flame

import com.coach.flame.aspect.LoggingServiceConfig
import com.coach.flame.client.ClientModuleConfig
import com.coach.flame.dailyTask.DailyTaskModuleConfig
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
        ClientModuleConfig::class,
        DailyTaskModuleConfig::class,
        LoggingServiceConfig::class]
)
class FlameCoachServiceApplication