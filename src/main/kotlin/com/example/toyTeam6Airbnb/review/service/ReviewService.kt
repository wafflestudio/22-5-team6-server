package com.example.toyTeam6Airbnb.review.service

import com.example.toyTeam6Airbnb.review.controller.ReviewByRoomDTO
import com.example.toyTeam6Airbnb.review.controller.ReviewByUserDTO
import com.example.toyTeam6Airbnb.review.controller.ReviewDTO
import com.example.toyTeam6Airbnb.review.controller.ReviewIdWithImage
import com.example.toyTeam6Airbnb.user.controller.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReviewService {

    fun createReview(
        user: User,
        reservationId: Long,
        content: String,
        rating: Int
    ): ReviewIdWithImage

    fun getReviewsByRoom(
        roomId: Long,
        pageable: Pageable
    ): Page<ReviewByRoomDTO>

    fun getReviewDetails(
        reviewId: Long
    ): ReviewDTO

    fun getReviewsByUser(
        viewerId: Long?,
        userId: Long,
        pageable: Pageable
    ): Page<ReviewByUserDTO>

    fun updateReview(
        user: User,
        reviewId: Long,
        content: String?,
        rating: Int?
    ): ReviewIdWithImage

    fun deleteReview(
        user: User,
        reviewId: Long
    )
}
