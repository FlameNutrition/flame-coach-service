package com.coach.flame.customer

import com.coach.flame.customer.security.HashPassword
import com.coach.flame.customer.security.Salt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile

@SpringBootApplication
@Profile(value = ["secureTool"])
@ConfigurationPropertiesScan("com.coach.flame.customer.props")
class FlameCoachSecureTool(
    private val hashPassword: HashPassword,
    private val salt: Salt,
) : CommandLineRunner {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(FlameCoachSecureTool::class.java)
    }

    override fun run(vararg args: String?) {

        LOGGER.info("opr='run', msg='Logging arguments', args={}", args)

        requireNotNull(args[0])

        val key = salt.generate()
        val password = args[0]?.let { hashPassword.generate(it, key) }

        LOGGER.info("opr='run', msg='Secure tool', key={}, passwordEncrypted={}", key, password)
        LOGGER.info("opr='run', msg='Password encrypted', args={}")


    }
}

fun main(args: Array<String>) {
    runApplication<FlameCoachSecureTool>(*args)
}
