package com.example.toyTeam6Airbnb.reservation.controller

import com.example.toyTeam6Airbnb.reservation.service.ReservationService
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.controller.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
import java.time.YearMonth

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation Controller", description = "Reservation Controller API")
class ReservationController(
    private val reservationService: ReservationService
) {

    @PostMapping
    @Operation(summary = "create Reservation", description = "create Reservation")
    fun createReservation(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: CreateReservationRequest
    ): ResponseEntity<ReservationDTO> {
        val reservation = reservationService.createReservation(
            User.fromEntity(principalDetails.getUser()),
            request.roomId,
            request.startDate,
            request.endDate
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation.toDTO())
    }

    @DeleteMapping("/{reservationId}")
    @Operation(summary = "delete Reservation", description = "delete Reservation")
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
    @Operation(summary = "update Reservation", description = "update Reservation")
    fun updateReservation(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reservationId: Long,
        @RequestBody request: UpdateReservationRequest
    ): ResponseEntity<ReservationDTO> {
        val reservation = reservationService.updateReservation(
            User.fromEntity(principalDetails.getUser()),
            reservationId,
            request.startDate,
            request.endDate
        )

        return ResponseEntity.ok().body(reservation.toDTO())
    }

    // 특정 reservation을 가져오는 API
    @GetMapping("/{reservationId}")
    fun getReservation(
        @PathVariable reservationId: Long
    ): ResponseEntity<ReservationDTO> {
        val reservation = reservationService.getReservation(reservationId)

        return ResponseEntity.ok(reservation.toDTO())
    }

    // 특정 user의 reservation을 모두 가져오는 API
    @GetMapping
    fun getReservationsByUser(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<List<ReservationDTO>> {
        val reservations = reservationService.getReservationsByUser(
            User.fromEntity(principalDetails.getUser())
        ).map { it.toDTO() }

        return ResponseEntity.ok(reservations)
    }

    // 특정 room의 reservation을 모두 가져오는 API
    @GetMapping("/room/{roomId}")
    fun getReservationsByRoom(
        @PathVariable roomId: Long
    ): ResponseEntity<List<ReservationDTO>> {
        val reservations = reservationService.getReservationsByRoom(roomId).map { it.toDTO() }

        return ResponseEntity.ok().body(reservations)
    }

    // 특정 date range의 reservation을 모두 가져오는 API
    @GetMapping("/date")
    fun getReservationsByDate(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ): ResponseEntity<List<ReservationDTO>> {
        val reservations = reservationService.getReservationsByDate(
            LocalDate.parse(startDate),
            LocalDate.parse(endDate)
        ).map { it.toDTO() }

        return ResponseEntity.ok().body(reservations)
    }

    // 특정 room의 특정 month의 available/unavailable date를 가져오는 API
    @GetMapping("/availability/{roomId}")
    @Operation(summary = "get Date Available by month", description = "get Date Available by month")
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
    val endDate: LocalDate
)

class UpdateReservationRequest(
    val startDate: LocalDate,
    val endDate: LocalDate
)

// 특정 방의 예약 가능날짜와 불가능한 날짜 반환 DTO
data class RoomAvailabilityResponse(
    val availableDates: List<LocalDate>,
    val unavailableDates: List<LocalDate>
)
