package com.example.toyTeam6Airbnb.config

import com.example.toyTeam6Airbnb.user.JwtAuthenticationFilter
import com.example.toyTeam6Airbnb.user.service.PrincipalDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val principalDetailsService: PrincipalDetailsService,
    private val customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder())
        provider.setUserDetailsService(principalDetailsService)
        return ProviderManager(provider)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(
            "http://localhost:5173",
            "https://d1m69dle8ss110.cloudfront.net",
            "https://d2gjarpl85ijp5.cloudfront.net"
        ) // Vite
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        configuration.allowedHeaders = listOf(
            "Origin",
            "X-Requested-With",
            "Content-Type",
            "Authorization",
            "Location"
        )
        configuration.exposedHeaders = listOf("Authorization", "Location")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { configurationSource = corsConfigurationSource() }
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize("/error", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/api-docs/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/swagger-resources/**", permitAll)

                // APIs that do not require authentication
                authorize("/api/auth/**", permitAll)
                authorize("/api/oauth2/**", permitAll)
                authorize(HttpMethod.GET, "/api/v1/rooms/main/**", permitAll)
                authorize(HttpMethod.GET, "/api/v1/reservations/availability/**", permitAll)
                authorize(HttpMethod.GET, "/api/v1/reviews/**", permitAll)
                authorize("/error", permitAll)
                authorize("/redirect", permitAll)
                authorize(anyRequest, authenticated)
            }
            formLogin {
                loginProcessingUrl = "/api/auth/login"
                usernameParameter = "username"
                passwordParameter = "password"
                authenticationSuccessHandler = customAuthenticationSuccessHandler
                authenticationFailureHandler = SimpleUrlAuthenticationFailureHandler()
            }
            oauth2Login {
                authorizationEndpoint {
                    baseUri = "/api/oauth2/authorization"
                }
                redirectionEndpoint {
                    baseUri = "/api/oauth2/callback/*"
                }
                authenticationSuccessHandler = customAuthenticationSuccessHandler
                authenticationFailureHandler = SimpleUrlAuthenticationFailureHandler()
            }
            logout {
                logoutUrl = "/api/auth/logout"
                logoutSuccessHandler = HttpStatusReturningLogoutSuccessHandler()
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }
        return http.build()
    }
}
