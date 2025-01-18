package com.example.toyTeam6Airbnb.user.controller

import com.example.toyTeam6Airbnb.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "User Controller", description = "User Controller API")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/api/v1/ping")
    @Operation(summary = "Ping pong", description = "Sample ping pong api for testing")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }

    @GetMapping("/api/v1/ping")
    fun getPing(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }

    @PostMapping("/api/auth/register")
    @Operation(summary = "회원가입", description = "유저 생성 및 프로필 이미지 업로드 URL 제공", hidden = true)
    fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<UrlResponse> {
        // Url 함게 반환하도록 수정
        val (user, url) = userService.register(request)
        return ResponseEntity.ok(UrlResponse(url))
    }

    // a mapping just for swagger testing
    // token parameter is passed as a query parameter
    // just return the token parameter in body
    @Operation(summary = "Redirect", description = "Redirect to the token", hidden = true)
    @GetMapping("/redirect")
    fun redirect(@RequestParam token: String, @RequestParam userid: Long): ResponseEntity<RedirectResponse> {
        return ResponseEntity.ok(RedirectResponse(token, userid))
    }
}

data class RegisterRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val bio: String,
    val showMyReviews: Boolean,
    val showMyReservations: Boolean
)

data class RedirectResponse(
    val token: String,
    val userId: Long
)

data class UrlResponse(
    val imageUploadurl: String
)
