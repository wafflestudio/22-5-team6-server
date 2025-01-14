package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import com.example.toyTeam6Airbnb.room.service.RoomService
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.controller.User
import io.swagger.v3.oas.annotations.Operation
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Room Controller", description = "Room Controller API")
class RoomController(
    private val roomService: RoomService
) {
    @PostMapping("/rooms")
    @Operation(summary = "방 생성", description = "방을 생성합니다")
    fun createRoom(
        @RequestBody request: CreateRoomRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<RoomDetailsDTO> {
        val room = roomService.createRoom(
            hostId = User.fromEntity(principalDetails.getUser()).id,
            name = request.name,
            description = request.description,
            type = request.type,
            address = request.address,
            roomDetails = request.roomDetails,
            price = request.price,
            maxOccupancy = request.maxOccupancy
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(room)
    }

    @GetMapping("/rooms/main")
    @Operation(summary = "메인 페이지 방 조회", description = "메인 페이지 용 방 목록을 조회합니다(페이지네이션 적용)")
    fun getRooms(
        pageable: Pageable
    ): ResponseEntity<Page<Room>> {
        val rooms = roomService.getRooms(pageable)
        return ResponseEntity.ok(rooms)
    }

    @GetMapping("/rooms/main/{roomId}")
    @Operation(summary = "방 상세 조회", description = "특정 방의 상세 정보를 조회합니다")
    fun getRoomDetails(
        @PathVariable roomId: Long
    ): ResponseEntity<RoomDetailsDTO> {
        val room = roomService.getRoomDetails(roomId)
        return ResponseEntity.ok(room)
    }

    @PutMapping("/rooms/{roomId}")
    @Operation(summary = "방 정보 수정", description = "방의 정보를 수정합니다")
    fun updateRoom(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable roomId: Long,
        @RequestBody request: UpdateRoomRequest
    ): ResponseEntity<RoomDetailsDTO> {
        val updatedRoom = roomService.updateRoom(
            User.fromEntity(principalDetails.getUser()).id,
            roomId,
            request.name,
            request.description,
            request.type,
            request.address,
            request.roomDetails,
            request.price,
            request.maxOccupancy
        )

        return ResponseEntity.ok(updatedRoom)
    }

    @DeleteMapping("/rooms/{roomId}")
    @Operation(summary = "방 삭제", description = "방을 삭제합니다")
    fun deleteRoom(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable roomId: Long
    ): ResponseEntity<Unit> {
        roomService.deleteRoom(
            User.fromEntity(principalDetails.getUser()).id,
            roomId
        )
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/rooms/main/search")
    @Operation(summary = "방 검색", description = "방을 검색합니다(페이지네이션 적용)")
    fun searchRooms(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) type: RoomType?,
        @RequestParam(required = false) minPrice: Double?,
        @RequestParam(required = false) maxPrice: Double?,
        @RequestParam(required = false) sido: String?,
        @RequestParam(required = false) sigungu: String?,
        @RequestParam(required = false) street: String?,
        @RequestParam(required = false) detail: String?,
        @RequestParam(required = false) maxOccupancy: Int?,
        @RequestParam(required = false) rating: Double?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        pageable: Pageable
    ): ResponseEntity<Page<Room>> {
        val address = AddressSearchDTO(sido, sigungu, street, detail)
        val rooms = roomService.searchRooms(name, type, minPrice, maxPrice, address, maxOccupancy, rating, startDate, endDate, pageable)
        return ResponseEntity.ok(rooms)
    }
}

data class AddressSearchDTO(
    val sido: String? = null,
    val sigungu: String? = null,
    val street: String? = null,
    val detail: String? = null
)

data class CreateRoomRequest(
    val name: String,
    val description: String,
    val type: RoomType,
    val address: Address,
    val roomDetails: RoomDetails,
    val price: Price,
    val maxOccupancy: Int
)

data class UpdateRoomRequest(
    val name: String,
    val description: String,
    val type: RoomType,
    val address: Address,
    val roomDetails: RoomDetails,
    val price: Price,
    val maxOccupancy: Int
)
