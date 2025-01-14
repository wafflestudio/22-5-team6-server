package com.example.toyTeam6Airbnb.profile.service

import com.example.toyTeam6Airbnb.profile.controller.CreateProfileRequest
import com.example.toyTeam6Airbnb.profile.controller.Profile
import com.example.toyTeam6Airbnb.profile.controller.UpdateProfileRequest
import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity

interface ProfileService {
    fun getCurrentUserProfile(
        user: UserEntity
    ): Profile

    fun updateCurrentUserProfile(
        user: UserEntity,
        request: UpdateProfileRequest
    ): Profile

    fun addProfileToCurrentUser(
        user: UserEntity,
        request: CreateProfileRequest
    ): Profile

    fun updateSuperHostStatus(
        profile: ProfileEntity
    )
}
