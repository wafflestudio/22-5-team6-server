package com.example.toyTeam6Airbnb.config

import com.example.toyTeam6Airbnb.user.JwtTokenProvider
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${spring.profiles.active}")
    private val profile: String
) : SimpleUrlAuthenticationSuccessHandler() {

    @Autowired
    @Lazy
    private lateinit var userService: UserService

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val token = jwtTokenProvider.generateToken(authentication.name)

        val principal = authentication.principal as PrincipalDetails

        response.sendRedirect("/redirect?token=$token&userid=${principal.getId()}&complete-profile=${!userService.hasProfile(authentication.name)}")
    }
}
