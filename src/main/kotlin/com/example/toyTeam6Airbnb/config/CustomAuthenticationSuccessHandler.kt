package com.example.toyTeam6Airbnb.config

import com.example.toyTeam6Airbnb.user.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${spring.profiles.active}")
    private val profile: String
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val token = jwtTokenProvider.generateToken(authentication.name)
        response.addHeader("Authorization", "Bearer $token")
        if(profile == "dev") response.sendRedirect("/redirect#token=$token")
    }
}
