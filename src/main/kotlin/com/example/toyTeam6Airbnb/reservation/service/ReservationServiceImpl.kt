package com.example.toyTeam6Airbnb.reservation.service

import com.example.toyTeam6Airbnb.reservation.ReservationUnavailable
import com.example.toyTeam6Airbnb.reservation.controller.Reservation
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class ReservationServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
): ReservationService {

    @Transactional
    override fun createReservation(
        user: User,
        roomId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): Reservation {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()

        if(!isAvailable(roomEntity, startDate, endDate)) throw ReservationUnavailable()

        val reservationEntity = ReservationEntity(
            user = userEntity,
            room = roomEntity,
            review = null,
            startDate = startDate,
            endDate = endDate,
            totalPrice = roomEntity.price * ChronoUnit.DAYS.between(startDate, endDate),
        ).let {
            reservationRepository.save(it)
        }

        return Reservation.fromEntity(reservationEntity)
    }

    fun isAvailable(room: RoomEntity, startDate: LocalDate, endDate: LocalDate): Boolean {
        val reservations = reservationRepository.findAllByRoom(room)

        return reservations.none { reservation ->
            startDate < reservation.endDate && endDate > reservation.startDate
        }
    }

    override fun getAllReservations(): List<Reservation> {
        return reservationRepository.findAll().map(Reservation::fromEntity)
    }

}