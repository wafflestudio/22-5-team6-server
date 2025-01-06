package com.example.toyTeam6Airbnb.reservation.controller

import com.example.toyTeam6Airbnb.reservation.service.ReservationService
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.controller.User
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation Controller", description = "Reservation Controller API")
class ReservationController(
    private val reservationService: ReservationService
) {

    @PostMapping
    fun createReservation(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: CreateReservationRequest,
    ): ResponseEntity<Reservation> {
        val reservation = reservationService.createReservation(
            User.fromEntity(principalDetails.getUser()),
            request.roomId,
            request.startDate,
            request.endDate,
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation)
    }

    @DeleteMapping("/{reservationId}")
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
    fun updateReservation(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reservationId: Long,
        @RequestBody request: UpdateReservationRequest
    ): ResponseEntity<Reservation> {
        val reservation = reservationService.updateReservation(
            User.fromEntity(principalDetails.getUser()),
            reservationId,
            request.startDate,
            request.endDate
        )

        return ResponseEntity.ok().body(reservation)
    }

    // 특정 reservation을 가져오는 API
    @GetMapping("/{reservationId}")
    fun getReservation(
        @PathVariable reservationId: Long
    ): ResponseEntity<Reservation> {
        val reservation = reservationService.getReservation(reservationId)

        return ResponseEntity.ok(reservation)
    }

    // 특정 user의 reservation을 모두 가져오는 API
    @GetMapping
    fun getReservationsByUser(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<List<Reservation>> {
        val reservations = reservationService.getReservationsByUser(
            User.fromEntity(principalDetails.getUser())
        )

        return ResponseEntity.ok(reservations)
    }

    // 특정 room의 reservation을 모두 가져오는 API
    @GetMapping("/room/{roomId}")
    fun getReservationsByRoom(
        @PathVariable roomId: Long
    ): ResponseEntity<List<Reservation>> {
        val reservations = reservationService.getReservationsByRoom(roomId)

        return ResponseEntity.ok().body(reservations)
    }

    // 특정 date range의 reservation을 모두 가져오는 API
    @GetMapping("/date")
    fun getReservationsByDate(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ): ResponseEntity<List<Reservation>> {
        val reservations = reservationService.getReservationsByDate(
            LocalDate.parse(startDate),
            LocalDate.parse(endDate)
        )

        return ResponseEntity.ok().body(reservations)
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