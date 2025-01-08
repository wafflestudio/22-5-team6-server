package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.user.controller.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RoomService {
    fun createRoom(
        host: User,
        name: String,
        description: String,
        type: String,
        address: Address,
        price: Double,
        maxOccupancy: Int
    ): Room

    fun getRooms(pageable: Pageable): Page<Room>

    fun getRoomDetails(roomId: Long): Room

    fun updateRoom(
        host: User,
        roomId: Long,
        name: String?,
        description: String?,
        type: String?,
        address: Address?,
        price: Double?,
        maxOccupancy: Int?
    ): Room

    fun deleteRoom(roomId: Long)

    fun searchRooms(
        name: String?,
        type: String?,
        minPrice: Double?,
        maxPrice: Double?,
        address: AddressSearchDTO?,
        maxOccupancy: Int?,
        rating: Double?,
        pageable: Pageable
    ): Page<Room>
}
