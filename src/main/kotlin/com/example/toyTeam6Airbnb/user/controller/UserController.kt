package com.example.toyTeam6Airbnb.user.controller

import com.example.toyTeam6Airbnb.user.service.UserServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
@Tag(name = "User Controller", description = "User Controller API")
class UserController(
    private val userService: UserServiceImpl
) {
    @PostMapping("/ping")
    @Operation(summary = "Ping pong", description = "Sample ping pong api for testing")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }

    @GetMapping("/ping")
    fun getPing(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }
}
