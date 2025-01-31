package com.example.toyTeam6Airbnb.user.controller

import com.example.toyTeam6Airbnb.user.persistence.AuthProvider

data class User(
    val id: Long,
    val username: String,
    val password: String,
    val provider: AuthProvider,
    val oAuthId: String?
) {
    companion object {
        fun fromEntity(entity: com.example.toyTeam6Airbnb.user.persistence.UserEntity): User {
            return User(
                id = entity.id!!,
                username = entity.username,
                password = entity.password,
                provider = entity.provider,
                oAuthId = entity.oAuthId
            )
        }
    }
}
