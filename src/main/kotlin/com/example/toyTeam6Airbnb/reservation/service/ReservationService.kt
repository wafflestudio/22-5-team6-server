package com.example.toyTeam6Airbnb.reservation.service

import com.example.toyTeam6Airbnb.reservation.controller.Reservation
import com.example.toyTeam6Airbnb.reservation.controller.RoomAvailabilityResponse
import com.example.toyTeam6Airbnb.user.controller.User
import java.time.LocalDate
import java.time.YearMonth

interface ReservationService {

    fun createReservation(
        user: User,
        roomId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): Reservation

    fun deleteReservation(
        user: User,
        reservationId: Long
    )

    fun updateReservation(
        user: User,
        reservationId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): Reservation

    fun getReservation(
        reservationId: Long
    ): Reservation

    fun getReservationsByUser(
        user: User
    ): List<Reservation>

    fun getReservationsByRoom(
        roomId: Long
    ): List<Reservation>

    fun getReservationsByDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Reservation>

    fun getAvailabilityByMonth(roomId: Long, yearMonth: YearMonth): RoomAvailabilityResponse
}
