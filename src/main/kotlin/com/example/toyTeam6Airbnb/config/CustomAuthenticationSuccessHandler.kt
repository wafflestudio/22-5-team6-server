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

//    override fun onAuthenticationSuccess(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        authentication: Authentication
//    ) {
//        val token = jwtTokenProvider.generateToken(authentication.name)
//        response.addHeader("Authorization", "Bearer $token")
//        if (profile == "prod") response.sendRedirect("/redirect?token=$token")
//    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val token = jwtTokenProvider.generateToken(authentication.name)
        response.addHeader("Authorization", "Bearer $token")

        if (profile == "prod") {
            // 배포 환경에서는 기존 리다이렉트 동작 유지
            response.sendRedirect("/redirect?token=$token")
        } else {
            // 로컬 환경에서는 JSON 응답 반환
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            response.writer.write("{\"token\": \"$token\"}")
            response.writer.flush()
        }
    }
}
