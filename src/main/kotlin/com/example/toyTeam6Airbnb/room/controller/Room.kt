package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import java.time.Instant

data class Room(
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
    val isSuperhost: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: com.example.toyTeam6Airbnb.room.persistence.RoomEntity): Room {
            var averageRating = entity.reviews.map { it.rating }.average()
            if (averageRating.isNaN()) averageRating = 0.0

            return Room(
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
                isSuperhost = entity.host.isSuperhost(),
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
            rating = this.rating
        )
    }

    fun toDetailsDTO(): RoomDetailsDTO {
        return RoomDetailsDTO(
            id = this.id,
            hostId = this.hostId,
            name = this.name,
            description = this.description,
            type = this.type,
            address = this.address,
            roomDetails = this.roomDetails,
            price = this.price,
            maxOccupancy = this.maxOccupancy,
            rating = this.rating,
            reviewCount = this.reviewCount,
            isSuperhost = this.isSuperhost,
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
    val price: Price,
    val maxOccupancy: Int,
    val rating: Double
)

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
    val isSuperhost: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
