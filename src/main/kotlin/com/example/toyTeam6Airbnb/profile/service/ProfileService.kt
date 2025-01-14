package com.example.toyTeam6Airbnb.profile.service

import com.example.toyTeam6Airbnb.profile.controller.CreateProfileRequest
import com.example.toyTeam6Airbnb.profile.controller.UpdateProfileRequest
import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity

interface ProfileService {
    fun getCurrentUserProfile(
        user: UserEntity
    ): ProfileEntity

    fun updateCurrentUserProfile(
        user: UserEntity,
        request: UpdateProfileRequest
    ): ProfileEntity

    fun addProfileToCurrentUser(
        user: UserEntity,
        request: CreateProfileRequest
    ): ProfileEntity

    fun updateSuperhostStatus(
        profile: ProfileEntity
    )
}
