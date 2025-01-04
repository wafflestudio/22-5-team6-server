package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.room.controller.Room

interface RoomService {
    fun addRoom(
        name: String,
        description: String,
        type: String,
        address: String,
        price: Double,
        maxOccupancy: Int
    ): Room

    fun getRooms()

    fun getRoomDetails()

    fun updateRoom()

    fun deleteRoom()
}
