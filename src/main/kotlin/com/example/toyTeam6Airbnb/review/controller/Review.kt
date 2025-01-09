package com.example.toyTeam6Airbnb.review.controller

import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import java.time.Instant

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

    fun toDTO(): ReviewDTO {
        return ReviewDTO(
            id = this.id,
            userId = this.userId,
            reservationId = this.reservationId,
            roomId = this.roomId,
            content = this.content,
            rating = this.rating
        )
    }
}

data class ReviewDTO(
    val id: Long,
    val userId: Long,
    val reservationId: Long,
    val roomId: Long,
    val content: String,
    val rating: Int
)
