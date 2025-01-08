package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import java.time.Instant

data class Room(
    val id: Long,
    val hostId: Long,
    val name: String,
    val description: String,
    val type: RoomType,
    val address: Address,
    val price: Double,
    val maxOccupancy: Int,
    val rating: Double,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: com.example.toyTeam6Airbnb.room.persistence.RoomEntity): Room {
            val averageRating = entity.reviews.map { it.rating }.average()
            return Room(
                id = entity.id!!,
                hostId = entity.host.id!!,
                name = entity.name,
                description = entity.description,
                type = entity.type,
                address = entity.address,
                price = entity.price,
                maxOccupancy = entity.maxOccupancy,
                rating = averageRating,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }

    fun toDTO(): RoomDTO {
        return RoomDTO(
            id = this.id,
            hostId = this.hostId,
            name = this.name,
            description = this.description,
            type = this.type,
            address = this.address,
            price = this.price,
            maxOccupancy = this.maxOccupancy,
            rating = this.rating,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}

data class RoomDTO(
    val id: Long,
    val hostId: Long,
    val name: String,
    val description: String,
    val type: RoomType,
    val address: Address,
    val price: Double,
    val maxOccupancy: Int,
    val rating: Double,
    val createdAt: Instant,
    val updatedAt: Instant
)
