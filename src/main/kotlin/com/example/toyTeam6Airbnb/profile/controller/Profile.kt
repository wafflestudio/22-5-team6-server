package com.example.toyTeam6Airbnb.profile.controller

import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity

data class Profile(
    val id: Long,
    val userId: Long,
    val nickname: String,
    val bio: String,
    val isSuperHost: Boolean
) {
    companion object {
        fun fromEntity(profileEntity: ProfileEntity): Profile {
            return Profile(
                id = profileEntity.id!!,
                userId = profileEntity.user.id!!,
                nickname = profileEntity.nickname,
                bio = profileEntity.bio,
                isSuperHost = profileEntity.isSuperHost
            )
        }
    }
}
