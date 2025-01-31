package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.controller.RoomByUserDTO
import com.example.toyTeam6Airbnb.room.controller.RoomDetailSearchDTO
import com.example.toyTeam6Airbnb.room.controller.RoomDetailsDTO
import com.example.toyTeam6Airbnb.room.controller.RoomShortDTO
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
        maxOccupancy: Int,
        imageSlot: Int // imageSlot Request Body에 추가
    ): RoomShortDTO

    fun getRooms(viewerId: Long?, pageable: Pageable): Page<Room>

    fun getRoomDetails(viewerId: Long?, roomId: Long): RoomDetailsDTO

    fun getRoomsByHostId(hostId: Long, pageable: Pageable): Page<RoomByUserDTO>

    fun updateRoom(
        hostId: Long,
        roomId: Long,
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        roomDetails: RoomDetails,
        price: Price,
        maxOccupancy: Int,
        imageSlot: Int // imageSlot Request Body에 추가
    ): RoomShortDTO

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
        roomDetails: RoomDetailSearchDTO?,
        viewerId: Long?,
        pageable: Pageable
    ): Page<Room>

    fun toggleLike(
        userId: Long,
        roomId: Long
    ): Boolean

    fun getHotPlacesByDate(
        viewerId: Long?,
        startDate: LocalDate,
        endDate: LocalDate
    ): Page<Room>

    fun getViewerId(): Long?
}
