package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

data class Room(
    val roomId: Long,
    val roomName: String,
    val roomType: RoomType,
    val sido: String,
    val sigungu: String,
    val price: Double,
    val averageRating: Double,
    val isLiked: Boolean,
    val imageUrl: String
) {
    companion object {
        fun fromEntity(entity: RoomEntity, imageUrl: String, isLiked: Boolean): Room {
            return Room(
                roomId = entity.id!!,
                roomName = entity.name,
                roomType = entity.type,
                sido = entity.address.sido,
                sigungu = entity.address.sigungu,
                price = entity.price.perNight,
                averageRating = BigDecimal.valueOf(entity.ratingStatistics.averageRating).setScale(2, RoundingMode.HALF_UP).toDouble(),
                isLiked = isLiked,
                imageUrl = imageUrl
            )
        }
    }
}

data class RoomDetailsDTO(
    val roomId: Long,
    val hostId: Long,
    val hostName: String?,
    val roomName: String,
    val description: String,
    val roomType: RoomType,
    val address: Address,
    val roomDetails: RoomDetails,
    val price: Price,
    val maxOccupancy: Int,
    val averageRating: Double,
    val reviewCount: Int,
    val isSuperHost: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isLiked: Boolean,
    val imageUrlList: List<String>
) {
    companion object {
        fun fromEntity(entity: RoomEntity, imageUrlList: List<String>, isLiked: Boolean): RoomDetailsDTO {
            return RoomDetailsDTO(
                roomId = entity.id!!,
                hostId = entity.host.id!!,
                hostName = entity.host.profile?.nickname,
                roomName = entity.name,
                description = entity.description,
                roomType = entity.type,
                address = entity.address,
                roomDetails = entity.roomDetails,
                price = entity.price,
                maxOccupancy = entity.maxOccupancy,
                averageRating = BigDecimal.valueOf(entity.ratingStatistics.averageRating).setScale(2, RoundingMode.HALF_UP).toDouble(),
                reviewCount = entity.reviews.size,
                isSuperHost = entity.host.isSuperHost(),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isLiked = isLiked,
                imageUrlList = imageUrlList
            )
        }
    }
}

data class RoomShortDTO(
    val roomId: Long,
    val imageUploadUrlList: List<String>
) {
    companion object {
        fun fromEntity(entity: RoomEntity, imageUploadUrlList: List<String>): RoomShortDTO {
            return RoomShortDTO(
                roomId = entity.id!!,
                imageUploadUrlList = imageUploadUrlList
            )
        }
    }
}

data class RoomByUserDTO(
    val roomId: Long,
    val roomName: String,
    val description: String,
    val address: Address,
    val roomType: RoomType,
    val roomDetails: RoomDetails,
    val price: Price,
    val maxOccupancy: Int,
    val imageUrl: String,
    val isLiked: Boolean
) {
    companion object {
        fun fromEntity(entity: RoomEntity, imageUrl: String, isLiked: Boolean): RoomByUserDTO {
            return RoomByUserDTO(
                roomId = entity.id!!,
                roomName = entity.name,
                description = entity.description,
                roomType = entity.type,
                address = entity.address,
                roomDetails = entity.roomDetails,
                price = entity.price,
                maxOccupancy = entity.maxOccupancy,
                imageUrl = imageUrl,
                isLiked = isLiked
            )
        }
    }
}
