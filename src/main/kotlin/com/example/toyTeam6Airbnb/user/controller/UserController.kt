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
    fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<Unit> {
        userService.register(request)
        return ResponseEntity.ok().build()
    }

    // a mapping just for swagger testing
    // token parameter is passed as a query parameter
    // just return the token parameter in body
    @Operation(summary = "Redirect", description = "Redirect to the token", hidden = true)
    @GetMapping("/redirect")
    fun redirect(@RequestParam token: String): ResponseEntity<String> {
        return ResponseEntity.ok(token)
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
