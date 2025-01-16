package com.example.toyTeam6Airbnb.profile.controller

import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity

data class Profile(
    val userId: Long,
    val nickname: String,
    val bio: String,
    val isSuperHost: Boolean,
    val showMyReviews: Boolean,
    val showMyReservations: Boolean
) {
    companion object {
        fun fromEntity(profileEntity: ProfileEntity): Profile {
            return Profile(
                userId = profileEntity.user.id!!,
                nickname = profileEntity.nickname,
                bio = profileEntity.bio,
                isSuperHost = profileEntity.isSuperHost,
                showMyReviews = profileEntity.showMyReviews,
                showMyReservations = profileEntity.showMyReservations
            )
        }
    }
}
