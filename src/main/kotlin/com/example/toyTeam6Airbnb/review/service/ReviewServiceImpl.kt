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
import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.example.toyTeam6Airbnb.user.UserNotFoundException
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import com.example.toyTeam6Airbnb.validateSortedPageable
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

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
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val reservationEntity = reservationRepository.findByIdOrNull(reservationId) ?: throw ReservationNotFound()
        if (reservationEntity.user.id != user.id) throw ReviewPermissionDeniedException()

        try {
            val reviewEntity = ReviewEntity(
                user = userEntity,
                room = reservationEntity.room,
                content = content,
                rating = rating,
                reservation = reservationEntity,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            ).let {
                reviewRepository.save(it)
            }
            return ReviewIdWithImage.fromEntity(reviewEntity)
        } catch (e: DataIntegrityViolationException) {
            throw DuplicateReviewException()
        }
    }

    @Transactional
    override fun getReviewsByRoom(roomId: Long, pageable: Pageable): Page<ReviewByRoomDTO> {
        roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()

        val reviewEntities = reviewRepository.findAllByRoomId(roomId, validateSortedPageable(pageable))

        val reviews = reviewEntities.map { ReviewByRoomDTO.fromEntity(it) }
        return reviews
    }

    @Transactional
    override fun getReviewsByUser(viewerId: Long?, userId: Long, pageable: Pageable): Page<ReviewByUserDTO> {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        if (viewerId != userId && userEntity.profile?.showMyReviews != true) throw ReviewPermissionDeniedException()

        val reviewEntities = reviewRepository.findAllByUserId(userId, validateSortedPageable(pageable))

        val reviews = reviewEntities.map { review ->
            val imageUrl = imageService.generateRoomImageDownloadUrl(review.room.id!!)
            ReviewByUserDTO.fromEntity(review, imageUrl)
        }
        return reviews
    }

    @Transactional
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
        userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val reviewEntity = reviewRepository.findByIdOrNull(reviewId) ?: throw ReviewNotFoundException()
        if (reviewEntity.user.id != user.id) throw ReviewPermissionDeniedException()

        reviewEntity.content = content ?: reviewEntity.content
        reviewEntity.rating = rating ?: reviewEntity.rating

        reviewRepository.save(reviewEntity)
        return ReviewIdWithImage.fromEntity(reviewEntity)
    }

    @Transactional
    override fun deleteReview(user: User, reviewId: Long) {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val reviewEntity = reviewRepository.findByIdOrNull(reviewId) ?: throw ReviewNotFoundException()

        if (reviewEntity.user != userEntity) throw ReviewPermissionDeniedException()

        reviewRepository.delete(reviewEntity)
    }
}
