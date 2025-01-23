package com.example.toyTeam6Airbnb.config

import com.example.toyTeam6Airbnb.user.JwtTokenProvider
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.persistence.AuthProvider
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

        val principal = authentication.principal as PrincipalDetails

        response.sendRedirect("/redirect?token=$token&userid=${principal.getId()}&complete-profile=${principal.getUser().profile == null}")
    }
}
