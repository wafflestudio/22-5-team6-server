package com.example.toyTeam6Airbnb.reservation.controller

import com.example.toyTeam6Airbnb.reservation.service.ReservationService
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
import java.time.YearMonth

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation Controller", description = "Reservation Controller API")
class ReservationController(
    private val reservationService: ReservationService
) {

    @PostMapping
    @Operation(summary = "예약 생성", description = "예약을 생성합니다")
    fun createReservation(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: CreateReservationRequest
    ): ResponseEntity<Reservation> {
        val reservation = reservationService.createReservation(
            User.fromEntity(principalDetails.getUser()),
            request.roomId,
            request.startDate,
            request.endDate,
            request.numberOfGuests
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation)
    }

    @DeleteMapping("/{reservationId}")
    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다")
    fun deleteReservation(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reservationId: Long
    ): ResponseEntity<Unit> {
        reservationService.deleteReservation(
            User.fromEntity(principalDetails.getUser()),
            reservationId
        )

        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{reservationId}")
    @Operation(summary = "예약 수정", description = "예약을 수정합니다")
    fun updateReservation(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reservationId: Long,
        @RequestBody request: UpdateReservationRequest
    ): ResponseEntity<Reservation> {
        val reservation = reservationService.updateReservation(
            User.fromEntity(principalDetails.getUser()),
            reservationId,
            request.startDate,
            request.endDate,
            request.numberOfGuests
        )

        return ResponseEntity.ok().body(reservation)
    }

    @GetMapping("/{reservationId}")
    @Operation(summary = "예약 상세 조회", description = "예약 상세 정보를 조회합니다. 예약한 숙소 이미지 조회 URL 제공")
    fun getReservation(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reservationId: Long
    ): ResponseEntity<ReservationDetails> {
        val reservation = reservationService.getReservation(principalDetails.getUser().id!!, reservationId)

        return ResponseEntity.ok(reservation)
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "유저별 예약 조회", description = "특정 유저의 모든 예약 정보를 조회합니다. 숙소 이미지 조회 URL 제공")
    fun getReservationsByUser(
        @PathVariable userId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<ReservationDTO>> {
        val viewerId =
            try {
                val principalDetails = SecurityContextHolder.getContext().authentication.principal as PrincipalDetails
                principalDetails.getUser().id
                // logic for when the user is logged in
            } catch (e: ClassCastException) {
                // logic for when the user is not logged in
                null
            }
        val reservations = reservationService.getReservationsByUser(viewerId, userId, pageable)
        return ResponseEntity.ok(reservations)
    }

    // 특정 room의 특정 month의 available/unavailable date를 가져오는 API
    @GetMapping("/availability/{roomId}")
    @Operation(summary = "해당 월의 예약 가능 날짜", description = "특정 방의 특정 월에 예약 가능/불가능한 모든 날짜 조회")
    fun getRoomAvailabilityByMonth(
        @PathVariable roomId: Long,
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<RoomAvailabilityResponse> {
        val roomAvailability = reservationService.getAvailabilityByMonth(
            roomId,
            YearMonth.of(year, month)
        )

        return ResponseEntity.ok().body(roomAvailability)
    }
}

class CreateReservationRequest(
    val roomId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val numberOfGuests: Int
)

class UpdateReservationRequest(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val numberOfGuests: Int
)

// 특정 방의 예약 가능날짜와 불가능한 날짜 반환 DTO
data class RoomAvailabilityResponse(
    val availableDates: List<LocalDate>,
    val unavailableDates: List<LocalDate>
)
