package com.example.toyTeam6Airbnb.reservation.controller

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import java.time.LocalDate

data class Reservation(
    val reservationId: Long
) {
    companion object {
        fun fromEntity(entity: ReservationEntity): Reservation {
            return Reservation(
                reservationId = entity.id!!
            )
        }
    }
}

data class ReservationDetails(
    val reservationId: Long,
    val userId: Long,
    val roomId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val place: String,
    val numberOfGuests: Int
    // val imageUrl: String,
) {
    companion object {
        fun fromEntity(entity: ReservationEntity): ReservationDetails {
            return ReservationDetails(
                reservationId = entity.id!!,
                userId = entity.user.id!!,
                roomId = entity.room.id!!,
                startDate = entity.startDate,
                endDate = entity.endDate,
                place = entity.room.address.sido,
                numberOfGuests = entity.numberOfGuests
                // imageUrl = entity.room.imageUrl
            )
        }
    }
}

data class ReservationDTO(
    val reservationId: Long,
    val place: String,
    val startDate: LocalDate,
    val endDate: LocalDate
    // val imageUrl: String,
) {
    companion object {
        fun fromEntity(entity: ReservationEntity): ReservationDTO {
            return ReservationDTO(
                reservationId = entity.id!!,
                place = entity.room.address.sido,
                startDate = entity.startDate,
                endDate = entity.endDate
                // imageUrl = entity.room.imageUrl
            )
        }
    }
}
