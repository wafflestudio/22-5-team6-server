package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.reservation.service.ReservationService
import com.example.toyTeam6Airbnb.room.InvalidAddressException
import com.example.toyTeam6Airbnb.room.InvalidDescriptionException
import com.example.toyTeam6Airbnb.room.InvalidMaxOccupancyException
import com.example.toyTeam6Airbnb.room.InvalidNameException
import com.example.toyTeam6Airbnb.room.InvalidPriceException
import com.example.toyTeam6Airbnb.room.InvalidRoomTypeException
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.RoomPermissionDeniedException
import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class RoomServiceImpl(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val reservationService: ReservationService
) : RoomService {

    @Transactional
    override fun createRoom(
        hostId: Long,
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        price: Double,
        maxOccupancy: Int
    ): Room {
        val hostEntity = userRepository.findByIdOrNull(hostId) ?: throw AuthenticateException()

        validateRoomDetails(name, description, type, address, price, maxOccupancy)

        val roomEntity = RoomEntity(
            host = hostEntity,
            name = name,
            description = description,
            type = type,
            address = address,
            price = price,
            maxOccupancy = maxOccupancy,
            reservations = emptyList(),
            reviews = emptyList()
        ).let {
            roomRepository.save(it)
        }

        return Room.fromEntity(roomEntity)
    }

    @Transactional
    override fun getRooms(pageable: Pageable): Page<Room> {
        return roomRepository.findAll(pageable).map { Room.fromEntity(it) }
    }

    @Transactional
    override fun getRoomDetails(roomId: Long): Room {
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()
        return Room.fromEntity(roomEntity)
    }

    @Transactional
    override fun updateRoom(
        hostId: Long,
        roomId: Long,
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        price: Double,
        maxOccupancy: Int
    ): Room {
        val hostEntity = userRepository.findByIdOrNull(hostId) ?: throw AuthenticateException()
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()

        if (roomEntity.host.id != hostEntity.id) {
            throw RoomPermissionDeniedException()
        }

        validateRoomDetails(name, description, type, address, price, maxOccupancy)

        roomEntity.name = name
        roomEntity.description = description
        roomEntity.type = type
        roomEntity.address = address
        roomEntity.price = price
        roomEntity.maxOccupancy = maxOccupancy

        roomRepository.save(roomEntity)
        return Room.fromEntity(roomEntity)
    }

    @Transactional
    override fun deleteRoom(
        userId: Long,
        roomId: Long
    ) {
        val hostEntity = userRepository.findByIdOrNull(userId) ?: throw AuthenticateException()
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()

        if (roomEntity.host.id != hostEntity.id) throw RoomPermissionDeniedException()

        roomRepository.delete(roomEntity)
    }

    @Transactional
    override fun searchRooms(
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
    ): Page<Room> {
        val rooms = roomRepository.searchAvailableRooms(
            name = name,
            type = type,
            minPrice = minPrice,
            maxPrice = maxPrice,
            address = address,
            maxOccupancy = maxOccupancy,
            rating = rating,
            startDate = startDate,
            endDate = endDate,
            pageable = pageable
        )
        return rooms.map { Room.fromEntity(it) }
    }

    private fun validateRoomDetails(
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        price: Double,
        maxOccupancy: Int
    ) {
        if (name.isBlank()) throw InvalidNameException()
        if (description.isBlank()) throw InvalidDescriptionException()
        if (price <= 0) throw InvalidPriceException()
        if (maxOccupancy <= 0) throw InvalidMaxOccupancyException()

        try {
            RoomType.valueOf(type.name)
        } catch (e: IllegalArgumentException) {
            throw InvalidRoomTypeException()
        }

        validateAddress(address)
    }

    private fun validateAddress(address: Address) {
        if (address.sido.isBlank() ||
            address.sigungu.isBlank() ||
            address.street.isBlank() ||
            address.detail.isBlank()
        ) {
            throw InvalidAddressException()
        }
    }
}
