package com.example.toyTeam6Airbnb.user.controller

import com.example.toyTeam6Airbnb.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/ping")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }
}
