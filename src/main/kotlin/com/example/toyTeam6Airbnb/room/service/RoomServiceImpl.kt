package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.RoomPermissionDeniedException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@Service
class RoomServiceImpl(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
) : RoomService {

    @Transactional
    override fun createRoom(
        host: User,
        name: String,
        description: String,
        type: String,
        address: String,
        price: Double,
        maxOccupancy: Int
    ): Room {
        val hostEntity = userRepository.findByIdOrNull(host.id) ?: throw AuthenticateException()

        val roomEntity =
            RoomEntity(
                host = hostEntity,
                name = name,
                description = description,
                type = type,
                address = address,
                price = price,
                maxOccupancy = maxOccupancy,
                reservations = emptyList(),
                reviews = emptyList(),
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
        host: User,
        roomId: Long,
        name: String?,
        description: String?,
        type: String?,
        address: String?,
        price: Double?,
        maxOccupancy: Int?
    ): Room {
        val hostEntity = userRepository.findByIdOrNull(host.id) ?: throw AuthenticateException()
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()

        if (roomEntity.host.id != hostEntity.id) {
            throw RoomPermissionDeniedException()
        }

        name?.let { roomEntity.name = it }
        description?.let { roomEntity.description = it }
        type?.let { roomEntity.type = it }
        address?.let { roomEntity.address = it }
        price?.let { roomEntity.price = it }
        maxOccupancy?.let { roomEntity.maxOccupancy = it }

        roomRepository.save(roomEntity)
        return Room.fromEntity(roomEntity)
    }

    @Transactional
    override fun deleteRoom(roomId: Long) {
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()
        roomRepository.delete(roomEntity)
    }
}
