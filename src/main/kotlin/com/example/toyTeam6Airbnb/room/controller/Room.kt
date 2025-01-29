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
    val sigungu: String,
    val price: Double,
    val averageRating: Double,
    val isLiked: Boolean,
    val imageUrl: String // 대표 이미지 1개만 return (downloadUrl)
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
                averageRating = entity.ratingStatistics.averageRating,
                isLiked = isLiked,
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
    val isLiked: Boolean,
    val imageUrlList: List<String> // 방에 대한 모든 Download url 전달
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
                averageRating = entity.ratingStatistics.averageRating,
                reviewCount = entity.reviews.size,
                isSuperHost = entity.host.isSuperhost(),
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
    val imageUploadUrlList: List<String> // 이미지가 여러 개면 List<String> 형태로 Upload Presigned URL 제공
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

// 호스트가 생성한 숙소들 목록을 제공하기 위해 만듬
// 숙소 수정/삭제를 위해서 제공
data class RoomByUserDTO(
    val roomId: Long,
    val roomName: String,
    val description: String,
    val address: Address,
    val roomType: RoomType,
    val roomDetails: RoomDetails,
    val price: Price,
    val maxOccupancy: Int,
    val imageUrl: String
) {
    companion object {
        fun fromEntity(entity: RoomEntity, imageUrl: String): RoomByUserDTO {
            return RoomByUserDTO(
                roomId = entity.id!!,
                roomName = entity.name,
                description = entity.description,
                roomType = entity.type,
                address = entity.address,
                roomDetails = entity.roomDetails,
                price = entity.price,
                maxOccupancy = entity.maxOccupancy,
                imageUrl = imageUrl
            )
        }
    }
}
