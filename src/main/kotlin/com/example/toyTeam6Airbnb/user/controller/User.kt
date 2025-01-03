package com.example.toyTeam6Airbnb.user.controller

data class User(
    val id: String,
    val username: String,
    val kakaoNickname: String?,
) {
    companion object {
        fun fromEntity(entity: com.example.toyTeam6Airbnb.user.persistence.UserEntity): User {
            return User(
                id = entity.id!!,
                username = entity.username,
                kakaoNickname = entity.kakaoNickname,
            )
        }
    }
}
