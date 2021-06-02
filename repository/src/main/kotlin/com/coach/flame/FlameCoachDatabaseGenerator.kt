package com.coach.flame

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources

@SpringBootApplication
@Profile(value = ["dbGenerator"])
@PropertySources(
    PropertySource("classpath:application-repository-db-generator.properties")
)
class FlameCoachDatabaseGenerator : CommandLineRunner {

    override fun run(vararg args: String?) {}

}

fun main(args: Array<String>) {
    runApplication<FlameCoachDatabaseGenerator>(*args)
}
