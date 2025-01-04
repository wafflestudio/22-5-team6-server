package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.room.service.RoomService
import com.example.toyTeam6Airbnb.user.AuthUser
import com.example.toyTeam6Airbnb.user.controller.User
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.http.ResponseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Room Controller", description = "Room Controller API")
class RoomController(
    private val roomService: RoomService
) {
    @PostMapping("/rooms")
    fun createRoom(
        @RequestBody request: CreateRoomRequest,
        @AuthUser user: User
    ): ResponseEntity<Room> {

        val room = roomService.createRoom(
            host = user,
            name = request.name,
            description = request.description,
            type = request.type,
            address = request.address,
            price = request.price,
            maxOccupancy = request.maxOccupancy
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(room)
    }

    @GetMapping("/rooms")
    fun getRooms(
        pageable: Pageable
    ): ResponseEntity<Page<Room>> {
        val rooms = roomService.getRooms(pageable)
        return ResponseEntity.ok(rooms)
    }

    @GetMapping("/rooms/{roomId}")
    fun getRoomDetails(
        @PathVariable roomId: Long
    ): ResponseEntity<Room> {
        val room = roomService.getRoomDetails(roomId)
        return ResponseEntity.ok(room)
    }

    @PutMapping("/rooms/{roomId}")
    fun updateRoom(
        @AuthUser user: User,
        @PathVariable roomId: Long,
        @RequestBody request: UpdateRoomRequest
    ): ResponseEntity<Room> {
        val updatedRoom = roomService.updateRoom(
            user,
            roomId,
            request.name,
            request.description,
            request.type,
            request.address,
            request.price,
            request.maxOccupancy
        )

        return ResponseEntity.ok(updatedRoom)
    }

    @DeleteMapping("/rooms/{roomId}")
    fun deleteRoom(
        @PathVariable roomId: Long
    ): ResponseEntity<Unit> {
        roomService.deleteRoom(roomId)
        return ResponseEntity.noContent().build()
    }
}

data class CreateRoomRequest(
    val name: String,
    val description: String,
    val type: String,
    val address: String,
    val price: Double,
    val maxOccupancy: Int
)

data class UpdateRoomRequest(
    val name: String?,
    val description: String?,
    val type: String?,
    val address: String?,
    val price: Double?,
    val maxOccupancy: Int?
)
