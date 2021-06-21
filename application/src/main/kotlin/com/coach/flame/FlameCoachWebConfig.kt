package com.coach.flame

import com.coach.flame.exception.handlers.SecurityExceptionHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * Config file to configure the WebSecurity and the WebMvc
 *
 * TODO: Implement a better way to secure the service
 */
@Configuration
@EnableWebSecurity
class FlameCoachWebConfig(
    private val securityExceptionHandler: SecurityExceptionHandler,
    @Value(value = "\${flamecoach.rest.security.password}") private val securityPassword: String,
) : WebSecurityConfigurerAdapter(), WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            //FIXME: This should be reviewed
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .maxAge(3600)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
            .withUser("admin")
            .password("{bcrypt}${securityPassword}")
            .roles("ADMIN")
    }

    override fun configure(http: HttpSecurity) {
        http.cors()
        http.csrf().disable()
        http.authorizeRequests().anyRequest().authenticated()
        http.exceptionHandling().authenticationEntryPoint(securityExceptionHandler)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.httpBasic()
    }

}
