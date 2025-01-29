package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.image.service.ImageService
import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity
import com.example.toyTeam6Airbnb.profile.persistence.ProfileRepository
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.persistence.RoomLikeRepository
import com.example.toyTeam6Airbnb.user.JwtTokenProvider
import com.example.toyTeam6Airbnb.user.LikedRoomsPermissionDenied
import com.example.toyTeam6Airbnb.user.SignUpBadUsernameException
import com.example.toyTeam6Airbnb.user.SignUpUsernameConflictException
import com.example.toyTeam6Airbnb.user.UserNotFoundException
import com.example.toyTeam6Airbnb.user.controller.RegisterRequest
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.AuthProvider
import com.example.toyTeam6Airbnb.user.persistence.RefreshTokenRepository
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import com.example.toyTeam6Airbnb.validatePageableForRoom
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val imageService: ImageService,
    private val passwordEncoder: PasswordEncoder,
    private val roomLikeRepository: RoomLikeRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider
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
            showMyReservations = request.showMyReservations,
            showMyWishlist = request.showMyWishlist // wishList 추가
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

    @Transactional
    override fun getLikedRooms(
        viewerId: Long?,
        userId: Long,
        pageable: Pageable
    ): Page<Room> {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        if (viewerId != userId && userEntity.profile?.showMyWishlist != true) throw LikedRoomsPermissionDenied()
        // 요청을 보낸 사용자가 본인이 아니고, 위시리스트 공개를 안한 경우에는 Permission Denied
        val roomEntities = roomLikeRepository.findRoomsLikedByUser(userEntity, validatePageableForRoom(pageable))

        return roomEntities.map { roomEntity ->
            val roomId = roomEntity.id!!
            val isLiked = true
            val imageUrl = imageService.generateRoomImageDownloadUrl(roomId)
            Room.fromEntity(roomEntity, imageUrl, isLiked)
        }
    }
}
