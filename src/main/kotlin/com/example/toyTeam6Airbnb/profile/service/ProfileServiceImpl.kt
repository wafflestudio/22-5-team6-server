package com.example.toyTeam6Airbnb.profile.service

import com.example.toyTeam6Airbnb.profile.ProfileAlreadyExistException
import com.example.toyTeam6Airbnb.profile.ProfileNotFoundException
import com.example.toyTeam6Airbnb.profile.controller.CreateProfileRequest
import com.example.toyTeam6Airbnb.profile.controller.UpdateProfileRequest
import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity
import com.example.toyTeam6Airbnb.profile.persistence.ProfileRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository
) : ProfileService {

    override fun getCurrentUserProfile(
        user: UserEntity
    ): ProfileEntity {
        return profileRepository.findByUser(user) ?: throw ProfileNotFoundException()
    }

    @Transactional
    override fun updateCurrentUserProfile(
        user: UserEntity,
        request: UpdateProfileRequest
    ): ProfileEntity {
        val profile = profileRepository.findByUser(user) ?: throw ProfileNotFoundException()

        profile.nickname = request.nickname
        profile.bio = request.bio
        updateSuperhostStatus(profile)

        return profileRepository.save(profile)
    }

    @Transactional
    override fun addProfileToCurrentUser(
        user: UserEntity,
        request: CreateProfileRequest
    ): ProfileEntity {
        if (profileRepository.existsByUser(user)) throw ProfileAlreadyExistException()

        val profile = ProfileEntity(user = user, nickname = request.nickname, bio = request.bio)
        updateSuperhostStatus(profile)
        return profileRepository.save(profile)
    }

    @Transactional
    override fun updateSuperhostStatus(profile: ProfileEntity) {
        val roomCount = roomRepository.countByHost(profile.user)
        profile.isSuperhost = roomCount >= 5
    }
}
