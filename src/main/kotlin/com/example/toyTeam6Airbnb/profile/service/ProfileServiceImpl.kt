package com.example.toyTeam6Airbnb.profile.service

import com.example.toyTeam6Airbnb.profile.controller.CreateProfileRequest
import com.example.toyTeam6Airbnb.profile.controller.UpdateProfileRequest
import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity
import com.example.toyTeam6Airbnb.profile.persistence.ProfileRepository
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val userRepository: UserRepository
) : ProfileService {

    override fun getCurrentUserProfile(@AuthenticationPrincipal user: UserEntity): ProfileEntity? {
        return profileRepository.findByUser(user)
    }

    @Transactional
    override fun updateCurrentUserProfile(user: UserEntity, request: UpdateProfileRequest): ProfileEntity {
        val profile = profileRepository.findByUser(user) ?: throw IllegalArgumentException("Profile not found")
        profile.nickname = request.nickname
        return profileRepository.save(profile)
    }

    @Transactional
    override fun addProfileToCurrentUser(user: UserEntity, request: CreateProfileRequest): ProfileEntity {
        if (profileRepository.findByUser(user) != null) {
            throw IllegalArgumentException("Profile already exists")
        }
        val profile = ProfileEntity(user = user, nickname = request.nickname)
        return profileRepository.save(profile)
    }
}
