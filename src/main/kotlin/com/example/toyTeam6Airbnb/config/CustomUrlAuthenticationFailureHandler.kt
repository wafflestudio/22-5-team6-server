package com.example.toyTeam6Airbnb.config

import com.example.toyTeam6Airbnb.user.SignInBadUsernameOrPasswordException
import com.example.toyTeam6Airbnb.user.SignInUnknownException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class CustomUrlAuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    private val klogger = KotlinLogging.logger {}

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        // map form login exceptions to custom exceptions
        klogger.error { "Authentication failed: ${exception.message}" }
        val customException = when (exception) {
            is BadCredentialsException -> SignInBadUsernameOrPasswordException()
            else -> SignInUnknownException()
        }

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json"
        response.writer.write(
            ObjectMapper().writeValueAsString(
                mapOf("error" to customException.msg, "errorCode" to customException.errorCode)
            )
        )
    }
}
