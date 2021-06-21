package com.coach.flame

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources

@SpringBootApplication
@Profile(value = ["dbGeneratorTool"])
@PropertySources(
    PropertySource("classpath:application-repository-db-generator.properties")
)
class FlameCoachDatabaseGenerator : CommandLineRunner {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(FlameCoachDatabaseGenerator::class.java)
    }

    override fun run(vararg args: String?) {
        LOGGER.info("opr='run', msg='Check the file: {}'", "ddl_jpa_creation.sql")
    }

}

fun main(args: Array<String>) {
    runApplication<FlameCoachDatabaseGenerator>(*args)
}
