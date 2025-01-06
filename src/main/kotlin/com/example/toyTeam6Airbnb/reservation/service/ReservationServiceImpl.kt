package com.example.toyTeam6Airbnb.reservation.service

import com.example.toyTeam6Airbnb.reservation.ReservationNotFound
import com.example.toyTeam6Airbnb.reservation.ReservationPermissionDenied
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

        if (!isAvailable(roomEntity, startDate, endDate)) throw ReservationUnavailable()

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

    @Transactional
    override fun deleteReservation(user: User, reservationId: Long) {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val reservationEntity = reservationRepository.findByIdOrNull(reservationId) ?: throw ReservationNotFound()

        if (reservationEntity.user != userEntity) throw ReservationPermissionDenied()

        reservationRepository.delete(reservationEntity)
    }

    @Transactional
    override fun updateReservation(
        user: User,
        reservationId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): Reservation {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val reservationEntity = reservationRepository.findByIdOrNull(reservationId) ?: throw ReservationNotFound()
        val roomEntity = reservationEntity.room

        if (reservationEntity.user != userEntity) throw ReservationPermissionDenied()

        if (!isAvailable(roomEntity, startDate, endDate)) throw ReservationUnavailable()

        reservationEntity.startDate = startDate
        reservationEntity.endDate = endDate
        reservationEntity.totalPrice = roomEntity.price * ChronoUnit.DAYS.between(startDate, endDate)
        reservationRepository.save(reservationEntity)

        return Reservation.fromEntity(reservationEntity)
    }

    @Transactional
    override fun getReservation(reservationId: Long): Reservation {
        val reservationEntity = reservationRepository.findByIdOrNull(reservationId) ?: throw ReservationNotFound()

        return Reservation.fromEntity(reservationEntity)
    }

    @Transactional
    override fun getReservationsByUser(user: User): List<Reservation> {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()

        return reservationRepository.findAllByUser(userEntity).map(Reservation::fromEntity)
    }

    @Transactional
    override fun getReservationsByRoom(roomId: Long): List<Reservation> {
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()

        return reservationRepository.findAllByRoom(roomEntity).map(Reservation::fromEntity)
    }

    @Transactional
    override fun getReservationsByDate(startDate: LocalDate, endDate: LocalDate): List<Reservation> {
        val reservations = reservationRepository.findAll().filter { reservation ->
            startDate < reservation.endDate && endDate > reservation.startDate
        }

        return reservations.map(Reservation::fromEntity)
    }
}