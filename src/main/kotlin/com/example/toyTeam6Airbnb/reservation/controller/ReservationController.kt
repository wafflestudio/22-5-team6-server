package com.example.toyTeam6Airbnb.reservation.controller

import com.example.toyTeam6Airbnb.reservation.service.ReservationService
import com.example.toyTeam6Airbnb.user.AuthUser
import com.example.toyTeam6Airbnb.user.controller.User
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservation Controller", description = "Reservation Controller API")
class ReservationController(
    private val reservationService: ReservationService
) {

    @PostMapping
    fun createReservation(
        @AuthUser user: User,
        @RequestBody request: CreateReservationRequest,
    ): ResponseEntity<Reservation> {
        val reservation = reservationService.createReservation(
            user,
            request.roomId,
            request.startDate,
            request.endDate,
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation)
    }

    @GetMapping
    fun getAllReservations() {
        // Implementation for getting all reservations of the current user
    }

    @GetMapping("/{reservationId}")
    fun getReservation(@PathVariable reservationId: Long) {
        // Implementation for getting a specific reservation by ID
    }

    @DeleteMapping("/{reservationId}")
    fun deleteReservation(@PathVariable reservationId: Long) {
        // Implementation for deleting a reservation by ID
    }

    @PutMapping("/{reservationId}")
    fun updateReservation(@PathVariable reservationId: Long) {
        // Implementation for updating a reservation by ID
    }

    @GetMapping("/room/{roomId}")
    fun getReservationsByRoom(@PathVariable roomId: Long) {
        // Implementation for getting all reservations for a specific room
    }

    @GetMapping("/date")
    fun getReservationsByDate(@RequestParam startDate: String, @RequestParam endDate: String) {
        // Implementation for getting reservations within a specific date range
    }
}

class CreateReservationRequest(
    val roomId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    // Request body for creating a reservation
}