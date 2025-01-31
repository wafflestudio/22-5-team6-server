package com.example.toyTeam6Airbnb.review.controller

import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import java.time.Instant
import java.time.LocalDate

data class ReviewByRoomDTO(
    val userId: Long,
    val nickname: String,
    val profileImage: String,
    val content: String,
    val rating: Int,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    companion object {
        fun fromEntity(entity: ReviewEntity, imageUrl: String): ReviewByRoomDTO {
            return ReviewByRoomDTO(
                userId = entity.user.id!!,
                nickname = entity.user.profile?.nickname ?: entity.user.username,
                profileImage = imageUrl,
                content = entity.content,
                rating = entity.rating,
                startDate = entity.reservation.startDate,
                endDate = entity.reservation.endDate
            )
        }
    }
}

data class ReviewByUserDTO(
    val reviewId: Long,
    val reservationId: Long,
    val roomId: Long,
    val content: String,
    val rating: Int,
    val place: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val imageUrl: String
) {
    companion object {
        fun fromEntity(entity: ReviewEntity, imageUrl: String): ReviewByUserDTO {
            return ReviewByUserDTO(
                reviewId = entity.id!!,
                reservationId = entity.reservation.id!!,
                roomId = entity.room.id!!,
                content = entity.content,
                rating = entity.rating,
                place = entity.room.address.sido,
                startDate = entity.reservation.startDate,
                endDate = entity.reservation.endDate,
                imageUrl = imageUrl
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

data class ReviewIdWithImage(
    val reviewId: Long,
    val imageUrl: String
) {
    companion object {
        fun fromEntity(entity: ReviewEntity, imageUrl: String): ReviewIdWithImage {
            return ReviewIdWithImage(
                reviewId = entity.id!!,
                imageUrl = imageUrl
            )
        }
    }
}
