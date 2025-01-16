package com.example.toyTeam6Airbnb.reservation.service

import com.example.toyTeam6Airbnb.reservation.controller.Reservation
import com.example.toyTeam6Airbnb.reservation.controller.ReservationDTO
import com.example.toyTeam6Airbnb.reservation.controller.ReservationDetails
import com.example.toyTeam6Airbnb.reservation.controller.RoomAvailabilityResponse
import com.example.toyTeam6Airbnb.user.controller.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.YearMonth

interface ReservationService {

    fun createReservation(
        user: User,
        roomId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        numberOfGuests: Int
    ): Reservation

    fun deleteReservation(
        user: User,
        reservationId: Long
    )

    fun updateReservation(
        user: User,
        reservationId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        numberOfGuests: Int
    ): Reservation

    fun getReservation(
        userId: Long,
        reservationId: Long
    ): ReservationDetails

    fun getReservationsByUser(
        viewerId: Long?,
        userId: Long,
        pageable: Pageable
    ): Page<ReservationDTO>

//    fun getReservationsByRoom(
//        roomId: Long
//    ): List<Reservation>
//
//    fun getReservationsByDate(
//        startDate: LocalDate,
//        endDate: LocalDate
//    ): List<Reservation>

    fun getAvailabilityByMonth(roomId: Long, yearMonth: YearMonth): RoomAvailabilityResponse
}
