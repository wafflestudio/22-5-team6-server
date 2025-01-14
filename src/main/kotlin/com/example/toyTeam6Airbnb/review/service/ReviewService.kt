package com.example.toyTeam6Airbnb.review.service

import com.example.toyTeam6Airbnb.review.controller.Review
import com.example.toyTeam6Airbnb.review.controller.ReviewDTO
import com.example.toyTeam6Airbnb.user.controller.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReviewService {

    fun createReview(
        roomId: Long,
        user: User,
        reservationId: Long,
        content: String,
        rating: Int
    ): Review

    fun getReviewsByRoom(
        roomId: Long,
        pageable: Pageable
    ): Page<ReviewDTO>

    fun getReviewDetails(
        reviewId: Long
    ): ReviewDTO

    fun getReviewsByUser(
        userId: Long,
        pageable: Pageable
    ): Page<ReviewDTO>

    fun updateReview(
        user: User,
        reviewId: Long,
        content: String?,
        rating: Int?
    ): Review

    fun deleteReview(
        user: User,
        reviewId: Long
    )
}
