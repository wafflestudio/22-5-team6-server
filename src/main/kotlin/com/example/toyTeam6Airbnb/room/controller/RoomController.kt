package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.room.service.RoomService
import com.example.toyTeam6Airbnb.room.validatePageable
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.controller.User
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Room Controller", description = "Room Controller API")
class RoomController(
    private val roomService: RoomService
) {
    @PostMapping("/rooms")
    fun createRoom(
        @RequestBody request: CreateRoomRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<RoomDTO> {
        val room = roomService.createRoom(
            host = User.fromEntity(principalDetails.getUser()),
            name = request.name,
            description = request.description,
            type = request.type,
            address = request.address,
            price = request.price,
            maxOccupancy = request.maxOccupancy
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(room.toDTO())
    }

    @GetMapping("/rooms")
    fun getRooms(
        pageable: Pageable
    ): ResponseEntity<Page<RoomDTO>> {
        // 정렬 기준 검증 및 기본값 처리
        val validatedPageable = validatePageable(pageable)
        val rooms = roomService.getRooms(validatedPageable).map { it.toDTO() }
        return ResponseEntity.ok(rooms)
    }

    @GetMapping("/rooms/{roomId}")
    fun getRoomDetails(
        @PathVariable roomId: Long
    ): ResponseEntity<RoomDTO> {
        val room = roomService.getRoomDetails(roomId)
        return ResponseEntity.ok(room.toDTO())
    }

    @PutMapping("/rooms/{roomId}")
    fun updateRoom(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable roomId: Long,
        @RequestBody request: UpdateRoomRequest
    ): ResponseEntity<RoomDTO> {
        val updatedRoom = roomService.updateRoom(
            User.fromEntity(principalDetails.getUser()),
            roomId,
            request.name,
            request.description,
            request.type,
            request.address,
            request.price,
            request.maxOccupancy
        )

        return ResponseEntity.ok(updatedRoom.toDTO())
    }

    @DeleteMapping("/rooms/{roomId}")
    fun deleteRoom(
        @PathVariable roomId: Long
    ): ResponseEntity<Unit> {
        roomService.deleteRoom(roomId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/rooms/search")
    fun searchRooms(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) minPrice: Double?,
        @RequestParam(required = false) maxPrice: Double?,
        @RequestParam(required = false) address: String?,
        @RequestParam(required = false) maxOccupancy: Int?,
        pageable: Pageable
    ): ResponseEntity<Page<RoomDTO>> {
        val validatedPage = validatePageable(pageable)
        val rooms = roomService.searchRooms(name, type, minPrice, maxPrice, address, maxOccupancy, validatedPage)
            .map { it.toDTO() }
        return ResponseEntity.ok(rooms)
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
