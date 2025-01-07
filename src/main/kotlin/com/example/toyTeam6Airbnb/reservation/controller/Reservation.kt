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
    val review: Review?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalPrice: Double,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: ReservationEntity): Reservation {
            return Reservation(
                id = entity.id!!,
                user = User.fromEntity(entity.user),
                room = Room.fromEntity(entity.room),
                review = entity.review?.let { Review.fromEntity(it) },
                startDate = entity.startDate,
                endDate = entity.endDate,
                totalPrice = entity.totalPrice,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }

    fun toDTO(): ReservationDTO {
        return ReservationDTO(
            id = this.id,
            userId = this.user.id,
            roomId = this.room.id,
            startDate = this.startDate,
            endDate = this.endDate
        )
    }
}
