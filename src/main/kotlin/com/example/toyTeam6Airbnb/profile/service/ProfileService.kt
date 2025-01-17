package com.example.toyTeam6Airbnb.profile.service

import com.example.toyTeam6Airbnb.profile.controller.CreateProfileRequest
import com.example.toyTeam6Airbnb.profile.controller.Profile
import com.example.toyTeam6Airbnb.profile.controller.UpdateProfileRequest
import com.example.toyTeam6Airbnb.profile.controller.UrlResponse
import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity

interface ProfileService {
    fun getCurrentUserProfile(
        user: UserEntity
    ): Profile

    fun getProfileByUserId(
        userId: Long
    ): Profile

    fun updateCurrentUserProfile(
        user: UserEntity,
        request: UpdateProfileRequest
    ): UrlResponse

    fun addProfileToCurrentUser(
        user: UserEntity,
        request: CreateProfileRequest
    ): UrlResponse

    fun updateSuperHostStatus(
        profile: ProfileEntity
    )
}
