package com.example.toyTeam6Airbnb.room.controller

import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomType
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDate

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

        return ResponseEntity.status(HttpStatus.CREATED).body(room.toDetailsDTO())
    }

    @GetMapping("/rooms/main")
    fun getRooms(
        pageable: Pageable
    ): ResponseEntity<Page<RoomDTO>> {
        // 정렬 기준 검증 및 기본값 처리
        val validatedPageable = validatePageable(pageable)
        val rooms = roomService.getRooms(validatedPageable).map { it.toDTO() }
        return ResponseEntity.ok(rooms)
    }

    @GetMapping("/rooms/main/{roomId}")
    fun getRoomDetails(
        @PathVariable roomId: Long
    ): ResponseEntity<RoomDetailsDTO> {
        val room = roomService.getRoomDetails(roomId)
        return ResponseEntity.ok(room.toDetailsDTO())
    }

    @GetMapping("/rooms/main/{roomId}/reviews")
    fun getRoomReviews(
        @PathVariable roomId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<RoomReviewDTO>> {
        val validatedPageable = validatePageable(pageable)
        val reviews = roomService.getRoomReviews(roomId, validatedPageable)
        return ResponseEntity.ok(reviews)
    }

    @PutMapping("/rooms/{roomId}")
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

        return ResponseEntity.ok(updatedRoom.toDetailsDTO())
    }

    @DeleteMapping("/rooms/{roomId}")
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
    ): ResponseEntity<Page<RoomDTO>> {
        val address = AddressSearchDTO(sido, sigungu, street, detail)
        val validatedPage = validatePageable(pageable)
        val rooms = roomService.searchRooms(name, type, minPrice, maxPrice, address, maxOccupancy, rating, startDate, endDate, validatedPage)
            .map { it.toDTO() }
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

data class RoomReviewDTO(
    val id: Long,
    val userId: Long,
    val rating: Int,
    val content: String,
    val reservationStartDate: LocalDate,
    val reservationEndDate: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun fromEntity(entity: ReviewEntity): RoomReviewDTO {
            return RoomReviewDTO(
                id = entity.id!!,
                userId = entity.user.id!!,
                rating = entity.rating,
                content = entity.content,
                reservationStartDate = entity.reservation.startDate,
                reservationEndDate = entity.reservation.endDate,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
