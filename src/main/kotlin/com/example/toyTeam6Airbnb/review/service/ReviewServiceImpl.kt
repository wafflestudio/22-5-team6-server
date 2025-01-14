package com.example.toyTeam6Airbnb.review.service
import com.example.toyTeam6Airbnb.reservation.ReservationNotFound
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import com.example.toyTeam6Airbnb.review.DuplicateReviewException
import com.example.toyTeam6Airbnb.review.ReviewNotFoundException
import com.example.toyTeam6Airbnb.review.ReviewPermissionDeniedException
import com.example.toyTeam6Airbnb.review.controller.Review
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewRepository
import com.example.toyTeam6Airbnb.profile.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ReviewServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
    private val reviewRepository: ReviewRepository
) : ReviewService {

    @Transactional
    override fun createReview(
        roomId: Long,
        user: User,
        reservationId: Long,
        content: String,
        rating: Int
    ): Review {
        // 1. userEntity, roomEntity, reservationEntity 가져오기 (없으면 예외처리)
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()
        val reservationEntity = reservationRepository.findByIdOrNull(reservationId) ?: throw ReservationNotFound()
        if (reservationEntity.user.id != user.id) throw ReviewPermissionDeniedException()

        try {
            val reviewEntity = ReviewEntity(
                user = userEntity,
                room = roomEntity,
                content = content,
                rating = rating,
                // 예약에 대해서는 리뷰가 있어야함. 예약 번호도 가져와야할듯.
                reservation = reservationEntity, // or provide a valid reservation
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            ).let {
                reviewRepository.save(it)
            }

            // 3. review 반환
            return Review.fromEntity(reviewEntity)
        } catch (e: DataIntegrityViolationException) {
            throw DuplicateReviewException()
        }
    }

    @Transactional
    override fun getReviews(roomId: Long): List<Review> {
        roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()

        val reviewEntities = reviewRepository.findAllByRoomId(roomId)

        val reviews = reviewEntities.map { Review.fromEntity(it) }
        return reviews
    }

    @Transactional
    override fun getReviewDetails(reviewId: Long): Review {
        val reviewEntity = reviewRepository.findByIdOrNull(reviewId) ?: throw ReviewNotFoundException()
        return Review.fromEntity(reviewEntity)
    }

    @Transactional
    override fun updateReview(user: User, reviewId: Long, content: String?, rating: Int?): Review {
        userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val reviewEntity = reviewRepository.findByIdOrNull(reviewId) ?: throw ReviewNotFoundException()
        if (reviewEntity.user.id != user.id) throw ReviewPermissionDeniedException()

        reviewEntity.content = content ?: reviewEntity.content
        reviewEntity.rating = rating ?: reviewEntity.rating

        reviewRepository.save(reviewEntity)

        return Review.fromEntity(reviewEntity)
    }

    @Transactional
    override fun deleteReview(user: User, reviewId: Long) {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val reviewEntity = reviewRepository.findByIdOrNull(reviewId) ?: throw ReviewNotFoundException()

        if (reviewEntity.user != userEntity) throw ReviewPermissionDeniedException()

        reviewRepository.delete(reviewEntity)
    }
}
