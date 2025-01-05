package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.user.controller.User

interface UserService {
    fun register(
        username: String,
        password: String
    ): User?
}
