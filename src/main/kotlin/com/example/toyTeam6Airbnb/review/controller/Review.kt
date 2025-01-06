package com.example.toyTeam6Airbnb.review.controller

import com.example.toyTeam6Airbnb.reservation.controller.Reservation
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.user.controller.User
import java.time.Instant

data class Review(
    val id: Long,
    val user: User,
    val reservation: Reservation,
    val room: Room,
    val content: String,
    val rating: Int,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: ReviewEntity): Review {
            return Review(
                id = entity.id!!,
                user = User.fromEntity(entity.user),
                reservation = Reservation.fromEntity(entity.reservation),
                room = Room.fromEntity(entity.room),
                content = entity.content,
                rating = entity.rating,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
