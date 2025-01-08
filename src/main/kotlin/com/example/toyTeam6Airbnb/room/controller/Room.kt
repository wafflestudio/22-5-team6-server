package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import java.time.Instant

data class Room(
    val id: Long,
    val host: UserEntity,
    val name: String,
    val description: String,
    val type: String,
    val address: String,
    val price: Double,
    val maxOccupancy: Int,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: com.example.toyTeam6Airbnb.room.persistence.RoomEntity): Room {
            return Room(
                id = entity.id!!,
                host = entity.host,
                name = entity.name,
                description = entity.description,
                type = entity.type,
                address = entity.address,
                price = entity.price,
                maxOccupancy = entity.maxOccupancy,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
    fun toDTO(): RoomDTO {
        return RoomDTO(
            id = this.id,
            hostId = this.host.id!!,
            name = this.name,
            description = this.description,
            type = this.type,
            address = this.address,
            price = this.price,
            maxOccupancy = this.maxOccupancy
        )
    }
}

data class RoomDTO(
    val id: Long,
    val hostId: Long,
    val name: String,
    val description: String,
    val type: String,
    val address: String,
    val price: Double,
    val maxOccupancy: Int
)