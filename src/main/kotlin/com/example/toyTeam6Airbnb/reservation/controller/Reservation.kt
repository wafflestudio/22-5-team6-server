package com.example.toyTeam6Airbnb.reservation.controller

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import java.time.Instant
import java.time.LocalDate

data class Reservation(
    val id: Long,
    val userId: Long,
    val roomId: Long,
    val reviewId: Long?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalPrice: Double,
    val numberOfGuests: Int,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: ReservationEntity): Reservation {
            return Reservation(
                id = entity.id!!,
                userId = entity.user.id!!,
                roomId = entity.room.id!!,
                reviewId = entity.review?.id,
                startDate = entity.startDate,
                endDate = entity.endDate,
                totalPrice = entity.totalPrice,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                numberOfGuests = entity.numberOfGuests
            )
        }
    }
}

data class ReservationDTO(
    val id: Long,
    val userId: Long,
    val roomId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val place: String,
    val numberOfGuests: Int
) {
    companion object {
        fun fromEntity(entity: ReservationEntity): ReservationDTO {
            return ReservationDTO(
                id = entity.id!!,
                userId = entity.user.id!!,
                roomId = entity.room.id!!,
                startDate = entity.startDate,
                endDate = entity.endDate,
                place = entity.room.address.sido,
                numberOfGuests = entity.numberOfGuests
            )
        }
    }
}
