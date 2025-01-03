package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import org.springframework.stereotype.Service

@Service
class RoomServiceImpl (
    private val roomRepository: RoomRepository
): RoomService {
    override fun addRoom(
        name: String,
        description: String,
        type: String,
        address: String,
        price: Double,
        maxOccupancy: Int
    ): Room {
        TODO("Not yet implemented")
    }

    override fun getRooms() {
        TODO("Not yet implemented")
    }

    override fun getRoomDetails() {
        TODO("Not yet implemented")
    }

    override fun updateRoom() {
        TODO("Not yet implemented")
    }

    override fun deleteRoom() {
        TODO("Not yet implemented")
    }

}
