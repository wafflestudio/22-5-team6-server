package com.example.toyTeam6Airbnb.config

import com.example.toyTeam6Airbnb.user.SignInBadUsernameOrPasswordException
import com.example.toyTeam6Airbnb.user.SignInUnknownException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class CustomUrlAuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        // map form login exceptions to custom exceptions
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
