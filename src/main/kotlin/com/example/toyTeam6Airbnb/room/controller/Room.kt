package com.example.toyTeam6Airbnb.room.controller

import java.time.Instant

data class Room(
    val id: Long,
    val hostId: Long,
    val name: String,
    val description: String?,
    val type: String?,
    val address: String,
    val price: Double,
    val maxOccupancy: Int,
    val facilities: String?,
    val interaction: String?,

    // review, reservation의 id만 전달
    val reservationIds: List<Long> = emptyList(),
    val reviewIds: List<Long> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: com.example.toyTeam6Airbnb.room.persistence.RoomEntity): Room {
            return Room(
                id = entity.id!!,
                hostId = entity.hostId,
                name = entity.name,
                description = entity.description,
                type = entity.type,
                address = entity.address,
                price = entity.price,
                maxOccupancy = entity.maxOccupancy,
                facilities = entity.facilities,
                interaction = entity.interaction,
                reservationIds = entity.reservations.map { it.id!! },
                reviewIds = entity.reviews.map { it.id!! },
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
