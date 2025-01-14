package com.example.toyTeam6Airbnb.review.controller

import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import java.time.Instant
import java.time.LocalDate

data class Review(
    val id: Long,
    val userId: Long,
    val reservationId: Long,
    val roomId: Long,
    val content: String,
    val rating: Int,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: ReviewEntity): Review {
            return Review(
                id = entity.id!!,
                userId = entity.user.id!!,
                reservationId = entity.reservation.id!!,
                roomId = entity.room.id!!,
                content = entity.content,
                rating = entity.rating,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}

data class ReviewDTO(
    val id: Long,
    val userId: Long,
    val reservationId: Long,
    val roomId: Long,
    val content: String,
    val rating: Int,
    val place: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: ReviewEntity): ReviewDTO {
            return ReviewDTO(
                id = entity.id!!,
                userId = entity.user.id!!,
                reservationId = entity.reservation.id!!,
                roomId = entity.room.id!!,
                content = entity.content,
                rating = entity.rating,
                place = entity.room.address.sido,
                startDate = entity.reservation.startDate,
                endDate = entity.reservation.endDate,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
