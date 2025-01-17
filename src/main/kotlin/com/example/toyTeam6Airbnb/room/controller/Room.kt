package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import java.time.Instant

data class Room(
    val roomId: Long,
    val roomName: String,
    val roomType: RoomType,
    val sido: String,
    val price: Double,
    val averageRating: Double,
    val imageUrl: String // 대표 이미지 1개만 return (downloadUrl)
) {
    companion object {
        fun fromEntity(entity: RoomEntity, imageUrl: String): Room {
            var averageRating = entity.reviews.map { it.rating }.average()
            if (averageRating.isNaN()) averageRating = 0.0
            return Room(
                roomId = entity.id!!,
                roomName = entity.name,
                roomType = entity.type,
                sido = entity.address.sido,
                price = entity.price.perNight,
                averageRating = averageRating,
                imageUrl = imageUrl // imageService에서 대표 이미지 url 가져오기, list<String>의 첫번째 값"
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
    val imageUrlList: List<String> // 방에 대한 모든 Download Presigned url 전달
) {
    companion object {
        fun fromEntity(entity: RoomEntity, imageUrlList: List<String>): RoomDetailsDTO {
            var averageRating = entity.reviews.map { it.rating }.average()
            if (averageRating.isNaN()) averageRating = 0.0

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
                averageRating = averageRating,
                reviewCount = entity.reviews.size,
                isSuperHost = entity.host.isSuperhost(),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                imageUrlList = imageUrlList
            )
        }
    }
}

data class RoomShortDTO(
    val roomId: Long,
    val imageUploadUrl: List<String> // 이미지가 여러 개면 List<String> 형태로 Upload Presigned URL 제공
) {
    companion object {
        fun fromEntity(entity: RoomEntity, imageUploadUrl: List<String>): RoomShortDTO {
            return RoomShortDTO(
                roomId = entity.id!!,
                imageUploadUrl = imageUploadUrl
            )
        }
    }
}
