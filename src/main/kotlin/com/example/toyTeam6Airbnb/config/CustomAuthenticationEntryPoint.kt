package com.example.toyTeam6Airbnb.config

import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val customException = AuthenticateException()

        val mapper = ObjectMapper()
        response.writer.write(
            ObjectMapper().writeValueAsString(
                mapOf("error" to customException.msg, "errorCode" to customException.errorCode)
            )
        )
    }
}
