package com.example.toyTeam6Airbnb.profile.controller

import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity

data class Profile(
    val id: Long,
    val userId: Long,
    val nickname: String
) {
    companion object {
        fun fromEntity(profileEntity: ProfileEntity): Profile {
            return Profile(
                id = profileEntity.id,
                userId = profileEntity.user.id!!,
                nickname = profileEntity.nickname
            )
        }
    }
}
