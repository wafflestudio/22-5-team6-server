package com.example.toyTeam6Airbnb.config

import com.example.toyTeam6Airbnb.user.JwtAuthenticationFilter
import com.example.toyTeam6Airbnb.user.service.PrincipalDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler

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
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize("/error", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize("/api/oauth2/**", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/api-docs/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/swagger-resources/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            formLogin {
                loginProcessingUrl = "/api/auth/login"
                usernameParameter = "username"
                passwordParameter = "password"
                authenticationSuccessHandler = customAuthenticationSuccessHandler
            }
            oauth2Login {
                authorizationEndpoint {
                    baseUri = "/api/oauth2/authorization"
                }
                redirectionEndpoint {
                    baseUri = "/api/oauth2/callback/*"
                }
                authenticationSuccessHandler = customAuthenticationSuccessHandler
            }
            logout {
                logoutUrl = "/api/auth/logout"
                logoutSuccessHandler = HttpStatusReturningLogoutSuccessHandler()
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            addFilterBefore<OAuth2AuthorizationRequestRedirectFilter>(ForwardedHeaderFilter())
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }
        return http.build()
    }
}
