package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import java.time.Instant

data class Room(
    val id: Long,
    val hostId: Long,
    val name: String,
    val description: String,
    val type: RoomType,
    val address: Address,
    val price: Price,
    val maxOccupancy: Int,
    val rating: Double
) {
    companion object {
        fun fromEntity(entity: RoomEntity): Room {
            var averageRating = entity.reviews.map { it.rating }.average()
            if (averageRating.isNaN()) averageRating = 0.0

            return Room(
                id = entity.id!!,
                hostId = entity.host.id!!,
                name = entity.name,
                description = entity.description,
                type = entity.type,
                address = entity.address,
                price = entity.price,
                maxOccupancy = entity.maxOccupancy,
                rating = averageRating
            )
        }
    }
}

data class RoomDetailsDTO(
    val id: Long,
    val hostId: Long,
    val name: String,
    val description: String,
    val type: RoomType,
    val address: Address,
    val roomDetails: RoomDetails,
    val price: Price,
    val maxOccupancy: Int,
    val rating: Double,
    val reviewCount: Int,
    val isSuperHost: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: RoomEntity): RoomDetailsDTO {
            var averageRating = entity.reviews.map { it.rating }.average()
            if (averageRating.isNaN()) averageRating = 0.0

            return RoomDetailsDTO(
                id = entity.id!!,
                hostId = entity.host.id!!,
                name = entity.name,
                description = entity.description,
                type = entity.type,
                address = entity.address,
                roomDetails = entity.roomDetails,
                price = entity.price,
                maxOccupancy = entity.maxOccupancy,
                rating = averageRating,
                reviewCount = entity.reviews.size,
                isSuperHost = entity.host.isSuperhost(),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
