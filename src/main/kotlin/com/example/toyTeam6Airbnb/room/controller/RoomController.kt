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
import org.springframework.security.core.context.SecurityContextHolder
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
    @Operation(summary = "방 생성", description = "방을 생성합니다, 이미지 업로드 URL 제공")
    fun createRoom(
        @RequestBody request: CreateRoomRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<RoomShortDTO> {
        val room = roomService.createRoom(
            hostId = User.fromEntity(principalDetails.getUser()).id,
            name = request.roomName,
            description = request.description,
            type = request.roomType,
            address = request.address,
            roomDetails = request.roomDetails,
            price = request.price,
            maxOccupancy = request.maxOccupancy,
            imageSlot = request.imageSlot
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(room)
    }

    @GetMapping("/rooms/main")
    @Operation(summary = "메인 페이지 방 조회", description = "메인 페이지 용 방 목록을 조회합니다(페이지네이션 적용), 대표이미지 조회 URL 제공")
    fun getRooms(
        pageable: Pageable
    ): ResponseEntity<Page<Room>> {
        val viewerId =
            try {
                val principalDetails = SecurityContextHolder.getContext().authentication.principal as PrincipalDetails
                principalDetails.getUser().id
                // logic for when the user is logged in
            } catch (e: ClassCastException) {
                // logic for when the user is not logged in
                null
            }
        val rooms = roomService.getRooms(viewerId, pageable)
        return ResponseEntity.ok(rooms)
    }

    @GetMapping("/rooms/main/{roomId}")
    @Operation(summary = "방 상세 조회", description = "특정 방의 상세 정보를 조회합니다. 방에 대한 모든 이미지에대해 조회 URL 제공")
    fun getRoomDetails(
        @PathVariable roomId: Long
    ): ResponseEntity<RoomDetailsDTO> {
        val viewerId =
            try {
                val principalDetails = SecurityContextHolder.getContext().authentication.principal as PrincipalDetails
                principalDetails.getUser().id
                // logic for when the user is logged in
            } catch (e: ClassCastException) {
                // logic for when the user is not logged in
                null
            }
        val room = roomService.getRoomDetails(viewerId, roomId)
        return ResponseEntity.ok(room)
    }

    @GetMapping("/rooms/{hostId}")
    @Operation(summary = "호스트 방 조회", description = "특정 호스트의 방 목록을 조회합니다(페이지네이션 적용), 대표이미지 조회 URL 제공")
    fun getRoomsByHostId(
        @PathVariable hostId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<RoomByUserDTO>> {
        val rooms = roomService.getRoomsByHostId(hostId, pageable)
        return ResponseEntity.ok(rooms)
    }

    @PutMapping("/rooms/{roomId}")
    @Operation(summary = "방 정보 수정", description = "방의 정보를 수정합니다, 이미지 업로드 URL 제공")
    fun updateRoom(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable roomId: Long,
        @RequestBody request: UpdateRoomRequest
    ): ResponseEntity<RoomShortDTO> {
        val updatedRoom = roomService.updateRoom(
            User.fromEntity(principalDetails.getUser()).id,
            roomId,
            request.roomName,
            request.description,
            request.roomType,
            request.address,
            request.roomDetails,
            request.price,
            request.maxOccupancy,
            request.imageSlot
        )

        return ResponseEntity.ok(updatedRoom)
    }

    @DeleteMapping("/rooms/{roomId}")
    @Operation(summary = "방 삭제", description = "방을 삭제합니다. 이미지도 삭제됨")
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
    @Operation(summary = "방 검색", description = "방을 검색합니다(페이지네이션 적용). 대표이미지 조회 URL 제공")
    fun searchRooms(
        @RequestParam(required = false) roomName: String?,
        @RequestParam(required = false) roomType: RoomType?,
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
        @RequestParam(required = false) wifi: Boolean?,
        @RequestParam(required = false) selfCheckin: Boolean?,
        @RequestParam(required = false) luggage: Boolean?,
        @RequestParam(required = false) tv: Boolean?,
        @RequestParam(required = false) bedRoom: Int?,
        @RequestParam(required = false) bathRoom: Int?,
        @RequestParam(required = false) bed: Int?,
        pageable: Pageable
    ): ResponseEntity<Page<Room>> {
        val viewerId =
            try {
                val principalDetails = SecurityContextHolder.getContext().authentication.principal as PrincipalDetails
                principalDetails.getUser().id
                // logic for when the user is logged in
            } catch (e: ClassCastException) {
                // logic for when the user is not logged in
                null
            }

        val address = AddressSearchDTO(sido, sigungu, street, detail)
        val roomDetails = RoomDetailSearchDTO(wifi, selfCheckin, luggage, tv, bedRoom, bathRoom, bed)
        val rooms = roomService.searchRooms(roomName, roomType, minPrice, maxPrice, address, maxOccupancy, rating, startDate, endDate, roomDetails, viewerId, pageable)
        return ResponseEntity.ok(rooms)
    }

    @PostMapping("/rooms/{roomId}/like")
    @Operation(summary = "방 좋아요(위시리스트 저장)", description = "유저가 방에대해 좋아요를 누릅니다(위시리스트 저장)")
    fun likeRoom(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable roomId: Long
    ): ResponseEntity<Unit> {
        roomService.likeRoom(
            userId = User.fromEntity(principalDetails.getUser()).id,
            roomId = roomId
        )
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @DeleteMapping("/rooms/{roomId}/like")
    @Operation(summary = "방 좋아요 해제", description = "유저가 기존 좋아요한 방에대해 좋아요 취소(위시리스트에서 제거).")
    fun unlikeRoom(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable roomId: Long
    ): ResponseEntity<Unit> {
        roomService.unlikeRoom(
            userId = User.fromEntity(principalDetails.getUser()).id,
            roomId = roomId
        )
        return ResponseEntity.noContent().build()
    }

    @GetMapping("rooms/main/hotPlaces")
    @Operation(summary = "특정 날짜 범위의 핫플 조회(추가 기능)", description = "특정 날짜 범위의 핫플을 조회합니다 (시군구 기준)")
    fun getHotPlaceByDate(
        @RequestParam startDate: LocalDate,
        @RequestParam endDate: LocalDate
    ): ResponseEntity<Page<Room>> {
        val viewerId =
            try {
                val principalDetails = SecurityContextHolder.getContext().authentication.principal as PrincipalDetails
                principalDetails.getUser().id
                // logic for when the user is logged in
            } catch (e: ClassCastException) {
                // logic for when the user is not logged in
                null
            }

        val hotPlaces = roomService.getHotPlacesByDate(viewerId, startDate, endDate)

        return ResponseEntity.ok().body(hotPlaces)
    }
}

data class AddressSearchDTO(
    val sido: String? = null,
    val sigungu: String? = null,
    val street: String? = null,
    val detail: String? = null
)

data class RoomDetailSearchDTO(
    val wifi: Boolean? = null,
    val selfCheckin: Boolean? = null,
    val luggage: Boolean? = null,
    val tv: Boolean? = null,
    val bedRoom: Int? = null,
    val bathRoom: Int? = null,
    val bed: Int? = null
)

data class CreateRoomRequest(
    val roomName: String,
    val description: String,
    val roomType: RoomType,
    val address: Address,
    val roomDetails: RoomDetails,
    val price: Price,
    val maxOccupancy: Int,
    val imageSlot: Int
)

data class UpdateRoomRequest(
    val roomName: String,
    val description: String,
    val roomType: RoomType,
    val address: Address,
    val roomDetails: RoomDetails,
    val price: Price,
    val maxOccupancy: Int,
    val imageSlot: Int
)
