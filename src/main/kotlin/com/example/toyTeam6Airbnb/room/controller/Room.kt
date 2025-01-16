package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import java.time.Instant

data class Room(
    val roomId: Long,
    val name: String,
    val type: RoomType,
    val sido: String,
    val price: Double,
    val averageRating: Double,
    //val imageUrl: String
) {
    companion object {
        fun fromEntity(entity: RoomEntity): Room {
            var averageRating = entity.reviews.map { it.rating }.average()
            if (averageRating.isNaN()) averageRating = 0.0

            return Room(
                roomId = entity.id!!,
                name = entity.name,
                type = entity.type,
                sido = entity.address.sido,
                price = entity.price.perNight,
                averageRating = averageRating,
                //imageUrl = entity.images.firstOrNull()?.url ?: ""
            )
        }
    }
}

data class RoomDetailsDTO(
    val roomId: Long,
    val hostId: Long,
    val name: String,
    val description: String,
    val type: RoomType,
    val address: Address,
    val roomDetails: RoomDetails,
    val price: Price,
    val maxOccupancy: Int,
    val averageRating: Double,
    val reviewCount: Int,
    val isSuperHost: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    //val imageUrl: String,
) {
    companion object {
        fun fromEntity(entity: RoomEntity): RoomDetailsDTO {
            var averageRating = entity.reviews.map { it.rating }.average()
            if (averageRating.isNaN()) averageRating = 0.0

            return RoomDetailsDTO(
                roomId = entity.id!!,
                hostId = entity.host.id!!,
                name = entity.name,
                description = entity.description,
                type = entity.type,
                address = entity.address,
                roomDetails = entity.roomDetails,
                price = entity.price,
                maxOccupancy = entity.maxOccupancy,
                averageRating = averageRating,
                reviewCount = entity.reviews.size,
                isSuperHost = entity.host.isSuperhost(),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                //imageUrl = entity.images.firstOrNull()?.url ?: ""
            )
        }
    }
}

data class RoomShortDTO(
    val roomId: Long,
    //val imageUrl: String,
) {
    companion object {
        fun fromEntity(entity: RoomEntity): RoomShortDTO {

            return RoomShortDTO(
                roomId = entity.id!!,
                //imageUrl = entity.images.firstOrNull()?.url ?: ""
            )
        }
    }
}
