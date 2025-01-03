package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.user.controller.User

interface UserService{
    fun signUp(
        username: String,
        password: String
    ): User

    fun signIn(
        username: String,
        password: String
    ): Pair<User, String>

    fun authenticate(
        accessToken: String,
    ): User
}
