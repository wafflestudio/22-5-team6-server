package com.example.toyTeam6Airbnb.profile.service

import com.example.toyTeam6Airbnb.image.service.ImageService
import com.example.toyTeam6Airbnb.profile.ProfileAlreadyExistException
import com.example.toyTeam6Airbnb.profile.ProfileNotFoundException
import com.example.toyTeam6Airbnb.profile.controller.CreateProfileRequest
import com.example.toyTeam6Airbnb.profile.controller.Profile
import com.example.toyTeam6Airbnb.profile.controller.UpdateProfileRequest
import com.example.toyTeam6Airbnb.profile.controller.UrlResponse
import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity
import com.example.toyTeam6Airbnb.profile.persistence.ProfileRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val roomRepository: RoomRepository,
    private val imageService: ImageService
) : ProfileService {

    @Transactional
    override fun getCurrentUserProfile(
        user: UserEntity
    ): Profile {
        val profile = profileRepository.findByUser(user) ?: throw ProfileNotFoundException()

        val imageUrl = imageService.generateProfileImageDownloadUrl(user.id!!)
        return Profile.fromEntity(profile, imageUrl)
    }

    @Transactional
    override fun getProfileByUserId(
        userId: Long
    ): Profile {
        val profile = profileRepository.findByUserId(userId) ?: throw ProfileNotFoundException()

        val imageUrl = imageService.generateProfileImageDownloadUrl(userId)
        return Profile.fromEntity(profile, imageUrl)
    }

    @Transactional
    override fun updateCurrentUserProfile(
        user: UserEntity,
        request: UpdateProfileRequest
    ): UrlResponse {
        val profile = profileRepository.findByUser(user) ?: ProfileEntity(user = user, nickname = "", bio = "")

        profile.nickname = request.nickname
        profile.bio = request.bio
        profile.showMyReviews = request.showMyReviews
        profile.showMyReservations = request.showMyReservations
        updateSuperHostStatus(profile)
        profileRepository.save(profile)

        return UrlResponse(imageService.generateProfileImageUploadUrl(user.id!!))
    }

    @Transactional
    override fun addProfileToCurrentUser(
        user: UserEntity,
        request: CreateProfileRequest
    ): UrlResponse {
        if (profileRepository.existsByUser(user)) throw ProfileAlreadyExistException()

        val profile = ProfileEntity(
            user = user,
            nickname = request.nickname,
            bio = request.bio,
            showMyReviews = request.showMyReviews,
            showMyReservations = request.showMyReservations
        )
        updateSuperHostStatus(profile)

        return UrlResponse(imageService.generateProfileImageUploadUrl(user.id!!))
    }

    @Transactional
    override fun updateSuperHostStatus(profile: ProfileEntity) {
        val roomCount = roomRepository.countByHost(profile.user)
        profile.isSuperHost = roomCount >= 5
    }
}
