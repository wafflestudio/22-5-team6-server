package com.example.toyTeam6Airbnb.user.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {
    @PostMapping("/signup")
    @Operation(summary = "Normal sign-up", description = "Basic sign up API")
    fun signup(@RequestBody signupRequest: SignupRequest): ResponseEntity<Any> {
        TODO()
        // 일반 회원가입 처리
    }

    @PostMapping("/login")
    @Operation(summary = "Normal login", description = "Basic login API")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        TODO()
        // 일반 로그인 처리
    }

    @GetMapping("/user")
    fun getCurrentUser(@AuthenticationPrincipal userPrincipal: CustomUserPrincipal): ResponseEntity<Any> {
        // 현재 인증된 사용자 정보 반환
    }
}

data class SignupRequest(
    val username: String,
    val password: String,
    val nickname: String,
)

data class LoginRequest(
    val username: String,
    val password: String,
)
