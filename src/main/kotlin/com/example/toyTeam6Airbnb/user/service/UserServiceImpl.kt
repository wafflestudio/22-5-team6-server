package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.image.ImageService
import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity
import com.example.toyTeam6Airbnb.profile.persistence.ProfileRepository
import com.example.toyTeam6Airbnb.user.SignUpBadUsernameException
import com.example.toyTeam6Airbnb.user.SignUpUsernameConflictException
import com.example.toyTeam6Airbnb.user.controller.RegisterRequest
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.AuthProvider
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val imageService: ImageService,
    private val passwordEncoder: PasswordEncoder
) : UserService {
    @Transactional
    override fun register(
        request: RegisterRequest
    ): Pair<User?, String> {
        if (request.username.startsWith("OAUTH")) throw SignUpBadUsernameException()
        if (userRepository.existsByUsername(request.username)) throw SignUpUsernameConflictException()
        val userEntity = UserEntity(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            provider = AuthProvider.LOCAL
        ).let {
            userRepository.save(it)
        }
        ProfileEntity(
            user = userEntity,
            nickname = request.nickname,
            bio = request.bio,
            showMyReviews = request.showMyReviews,
            showMyReservations = request.showMyReservations
        ).let { profileRepository.save(it) }
        val imageUploadUrl = imageService.generateProfileImageUploadUrl(userEntity.id!!)
        return User.fromEntity(userEntity) to imageUploadUrl
    }

    @Transactional
    override fun hasProfile(
        username: String
    ): Boolean {
        userRepository.findByUsername(username)?.profile ?: return false
        return true
    }
}
