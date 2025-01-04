package com.example.toyTeam6Airbnb.user.controller

import com.example.toyTeam6Airbnb.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "User Controller", description = "User Controller API")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/v1/ping")
    @Operation(summary = "Ping pong", description = "Sample ping pong api for testing")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }

    @GetMapping("/v1/ping")
    fun getPing(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }

    @PostMapping("/auth/register")
    fun register(
        @RequestBody request: registerRequest
    ): ResponseEntity<Unit> {
        userService.register(username = request.username, password = request.password)
        return ResponseEntity.ok().build()
    }
}

data class registerRequest(
    val username: String,
    val password: String
)
