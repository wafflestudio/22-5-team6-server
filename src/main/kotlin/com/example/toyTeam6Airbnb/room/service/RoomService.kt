package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.controller.RoomReviewDTO
import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface RoomService {
    fun createRoom(
        hostId: Long,
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        roomDetails: RoomDetails,
        price: Price,
        maxOccupancy: Int
    ): Room

    fun getRooms(pageable: Pageable): Page<Room>

    fun getRoomDetails(roomId: Long): Room

    fun getRoomReviews(roomId: Long, pageable: Pageable): Page<RoomReviewDTO>

    fun updateRoom(
        hostId: Long,
        roomId: Long,
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        roomDetails: RoomDetails,
        price: Price,
        maxOccupancy: Int
    ): Room

    fun deleteRoom(
        userId: Long,
        roomId: Long
    )

    fun searchRooms(
        name: String?,
        type: RoomType?,
        minPrice: Double?,
        maxPrice: Double?,
        address: AddressSearchDTO?,
        maxOccupancy: Int?,
        rating: Double?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<Room>
}
