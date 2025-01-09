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

        // JSON 형식으로 body에 token 추가
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write("{\"success\": true, \"token\": \"$token\"}")

        // 리다이렉트 URL 설정 (기존 동작 유지)
        response.sendRedirect("/redirect?token=$token")
    }
}
