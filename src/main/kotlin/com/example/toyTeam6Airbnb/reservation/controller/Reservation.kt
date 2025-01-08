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
    val numberofGuests : Int,
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
                numberofGuests = entity.numberOfGuests
            )
        }
    }

    fun toDTO(): ReservationDTO {
        return ReservationDTO(
            id = this.id,
            userId = this.userId,
            roomId = this.roomId,
            startDate = this.startDate,
            endDate = this.endDate,
            numberOfGuests = this.numberofGuests
        )
    }
}

data class ReservationDTO(
    val id: Long,
    val roomId: Long,
    val userId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val numberOfGuests : Int
)
