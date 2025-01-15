package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.user.controller.RegisterRequest
import com.example.toyTeam6Airbnb.user.controller.User

interface UserService {
    fun register(
        request: RegisterRequest
    ): User?
}
