package com.example.toyTeam6Airbnb.review.service

import com.example.toyTeam6Airbnb.image.service.ImageService
import com.example.toyTeam6Airbnb.reservation.ReservationNotFound
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import com.example.toyTeam6Airbnb.review.DuplicateReviewException
import com.example.toyTeam6Airbnb.review.ReviewNotFoundException
import com.example.toyTeam6Airbnb.review.ReviewPermissionDeniedException
import com.example.toyTeam6Airbnb.review.controller.ReviewByRoomDTO
import com.example.toyTeam6Airbnb.review.controller.ReviewByUserDTO
import com.example.toyTeam6Airbnb.review.controller.ReviewDTO
import com.example.toyTeam6Airbnb.review.controller.ReviewIdWithImage
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewRepository
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.UserNotFoundException
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import com.example.toyTeam6Airbnb.validatePageableForReview
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
    private val reviewRepository: ReviewRepository,
    private val imageService: ImageService
) : ReviewService {

    @Transactional
    override fun createReview(
        user: User,
        reservationId: Long,
        content: String,
        rating: Int
    ): ReviewIdWithImage {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw UserNotFoundException()
        val reservationEntity = reservationRepository.findByIdOrNull(reservationId) ?: throw ReservationNotFound()
        val roomEntity = roomRepository.findByIdOrNullForUpdate(reservationEntity.room.id!!) ?: throw RoomNotFoundException()
        if (reservationEntity.user.id != user.id) throw ReviewPermissionDeniedException()

        val reviewEntity = ReviewEntity(
            user = userEntity,
            room = reservationEntity.room,
            content = content,
            rating = rating,
            reservation = reservationEntity
        )

        try {
            reviewRepository.save(reviewEntity)
        } catch (e: DataIntegrityViolationException) {
            throw DuplicateReviewException()
        }

        roomEntity.ratingStatistics.incrementRating(rating)
        roomRepository.save(roomEntity)

        val imageUrl = imageService.generateRoomImageDownloadUrl(roomEntity.id!!)

        return ReviewIdWithImage.fromEntity(reviewEntity, imageUrl)
    }

    @Transactional(readOnly = true)
    override fun getReviewsByRoom(roomId: Long, pageable: Pageable): Page<ReviewByRoomDTO> {
        roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()

        val reviewEntities = reviewRepository.findAllByRoomId(roomId, validatePageableForReview(pageable))

        val reviews = reviewEntities.map {
            val userId = it.user.id!!
            val imageUrl = imageService.generateProfileImageDownloadUrl(userId)
            ReviewByRoomDTO.fromEntity(it, imageUrl)
        }
        return reviews
    }

    @Transactional(readOnly = true)
    override fun getReviewsByUser(viewerId: Long?, userId: Long, pageable: Pageable): Page<ReviewByUserDTO> {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        if (viewerId != userId && userEntity.profile?.showMyReviews != true) throw ReviewPermissionDeniedException()

        val reviewEntities = reviewRepository.findAllByUserId(userId, validatePageableForReview(pageable))

        val reviews = reviewEntities.map { review ->
            val imageUrl = imageService.generateRoomImageDownloadUrl(review.room.id!!)
            ReviewByUserDTO.fromEntity(review, imageUrl)
        }
        return reviews
    }

    @Transactional(readOnly = true)
    override fun getReviewDetails(reviewId: Long): ReviewDTO {
        val reviewEntity = reviewRepository.findByIdOrNull(reviewId) ?: throw ReviewNotFoundException()
        return ReviewDTO.fromEntity(reviewEntity)
    }

    @Transactional
    override fun updateReview(
        user: User,
        reviewId: Long,
        content: String?,
        rating: Int?
    ): ReviewIdWithImage {
        userRepository.findByIdOrNull(user.id) ?: throw UserNotFoundException()
        val reviewEntity = reviewRepository.findByIdOrNullForUpdate(reviewId)
            ?: throw ReviewNotFoundException()

        if (reviewEntity.user.id != user.id) throw ReviewPermissionDeniedException()

        val roomEntity = roomRepository.findByIdOrNullForUpdate(reviewEntity.room.id!!)
            ?: throw RoomNotFoundException()

        val previousRating = reviewEntity.rating
        reviewEntity.content = content ?: reviewEntity.content
        reviewEntity.rating = rating ?: reviewEntity.rating

        if (previousRating != reviewEntity.rating) {
            roomEntity.ratingStatistics.decrementRating(previousRating)
            roomEntity.ratingStatistics.incrementRating(reviewEntity.rating)
            roomRepository.save(roomEntity)
        }

        reviewRepository.save(reviewEntity)

        val imageUrl = imageService.generateRoomImageDownloadUrl(roomEntity.id!!)

        return ReviewIdWithImage.fromEntity(reviewEntity, imageUrl)
    }

    @Transactional
    override fun deleteReview(user: User, reviewId: Long) {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw UserNotFoundException()

        val reviewEntity = reviewRepository.findByIdOrNullForUpdate(reviewId)
            ?: throw ReviewNotFoundException()

        if (reviewEntity.user != userEntity) throw ReviewPermissionDeniedException()

        val roomEntity = roomRepository.findByIdOrNullForUpdate(reviewEntity.room.id!!)
            ?: throw RoomNotFoundException()

        val previousRating = reviewEntity.rating
        roomEntity.ratingStatistics.decrementRating(previousRating)
        roomRepository.save(roomEntity)

        reviewRepository.delete(reviewEntity)
    }
}
