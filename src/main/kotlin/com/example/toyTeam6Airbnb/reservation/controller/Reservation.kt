package com.example.toyTeam6Airbnb.reservation.controller

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.review.controller.Review
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.user.controller.User
import java.time.Instant
import java.time.LocalDate

data class Reservation(
    val id: Long,
    val user: User,
    val room: Room,
    val review: Review,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalPrice: Double,
    val createdAt: Instant
) {
    companion object {
        fun fromEntity(entity: ReservationEntity): Reservation {
            return Reservation(
                id = entity.id!!,
                user = User.fromEntity(entity.user),
                room = Room.fromEntity(entity.room),
                review = Review.fromEntity(entity.review),
                startDate = entity.startDate,
                endDate = entity.endDate,
                totalPrice = entity.totalPrice,
                createdAt = entity.createdAt
            )
        }
    }
}