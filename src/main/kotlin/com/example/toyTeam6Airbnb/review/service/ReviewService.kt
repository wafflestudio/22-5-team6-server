package com.example.toyTeam6Airbnb.review.service

import com.example.toyTeam6Airbnb.review.controller.Review
import com.example.toyTeam6Airbnb.user.controller.User
interface ReviewService {

    fun createReview(
        roomId: Long,
        user: User,
        reservationId: Long,
        content: String,
        rating: Int
    ): Review

    fun getReviews(
        roomId: Long
    ): List<Review>

    fun getReviewDetails(
        reviewId: Long
    ): Review

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
