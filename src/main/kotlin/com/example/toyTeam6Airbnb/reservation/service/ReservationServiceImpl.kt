package com.example.toyTeam6Airbnb.reservation.service

import com.example.toyTeam6Airbnb.reservation.MaxOccupancyExceeded
import com.example.toyTeam6Airbnb.reservation.ReservationNotFound
import com.example.toyTeam6Airbnb.reservation.ReservationPermissionDenied
import com.example.toyTeam6Airbnb.reservation.ReservationUnavailable
import com.example.toyTeam6Airbnb.reservation.ZeroGuests
import com.example.toyTeam6Airbnb.reservation.controller.Reservation
import com.example.toyTeam6Airbnb.reservation.controller.ReservationDTO
import com.example.toyTeam6Airbnb.reservation.controller.RoomAvailabilityResponse
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.example.toyTeam6Airbnb.user.UserNotFoundException
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@Service
class ReservationServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
    private val entityManager: EntityManager
) : ReservationService {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun createReservation(
        user: User,
        roomId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        numberOfGuests: Int
    ): Reservation {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        // lock room entity to prevent the room from being deleted while creating a reservation
        // also, prevent other transactions from creating a reservation for the same room at the same time
        val roomEntity = roomRepository.findByIdOrNullForUpdate(roomId) ?: throw RoomNotFoundException()

        if (!isAvailable(roomEntity, startDate, endDate)) throw ReservationUnavailable()

        // 예약 인원수가 초과할 경우 예외 발생
        if (numberOfGuests > roomEntity.maxOccupancy) throw MaxOccupancyExceeded()

        // 예약 인원이 0명인 경우 예외 발생
        if (numberOfGuests == 0) throw ZeroGuests()

        val reservationEntity = ReservationEntity(
            user = userEntity,
            room = roomEntity,
            review = null,
            startDate = startDate,
            endDate = endDate,
            totalPrice = roomEntity.price.total * ChronoUnit.DAYS.between(startDate, endDate),
            numberOfGuests = numberOfGuests
        ).let {
            reservationRepository.save(it)
        }

        return Reservation.fromEntity(reservationEntity)
    }

    fun isAvailable(room: RoomEntity, startDate: LocalDate, endDate: LocalDate, currentReservationId: Long? = null): Boolean {
        // exception 발생도 함께 처리
        if (startDate >= endDate) throw ReservationUnavailable()
        val reservations = reservationRepository.findAllByRoom(room)

        // 현재 예약 건을 제외하고, 다른 예약과 겹치는 여부를 확인함.
        // startDate < 기존 예약의 startDate && endDate > 기존 예약의 endDate, 즉 기존 예약을 포괄하는 경우도 제외하도록 조건수정
        return reservations.none { reservation ->
            reservation.id != currentReservationId && (startDate < reservation.endDate && endDate > reservation.startDate)
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun deleteReservation(user: User, reservationId: Long) {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val reservationEntity = reservationRepository.findByIdOrNullForUpdate(reservationId) ?: throw ReservationNotFound()

        if (reservationEntity.user != userEntity) throw ReservationPermissionDenied()

        reservationRepository.delete(reservationEntity)
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun updateReservation(
        user: User,
        reservationId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        numberOfGuests: Int
    ): Reservation {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw AuthenticateException()
        val reservationEntity = reservationRepository.findByIdOrNullForUpdate(reservationId) ?: throw ReservationNotFound()
        val roomEntity = reservationEntity.room
        // lock room entity to prevent concurrent reservation updates
        entityManager.lock(roomEntity, LockModeType.PESSIMISTIC_WRITE)

        if (reservationEntity.user != userEntity) throw ReservationPermissionDenied()

        if (!isAvailable(roomEntity, startDate, endDate, reservationEntity.id)) throw ReservationUnavailable()

        if (numberOfGuests > roomEntity.maxOccupancy) throw MaxOccupancyExceeded()
        if (numberOfGuests == 0) throw ZeroGuests()

        reservationEntity.startDate = startDate
        reservationEntity.endDate = endDate
        reservationEntity.totalPrice = roomEntity.price.total * ChronoUnit.DAYS.between(startDate, endDate)
        reservationEntity.numberOfGuests = numberOfGuests
        reservationRepository.save(reservationEntity)

        return Reservation.fromEntity(reservationEntity)
    }

    @Transactional
    override fun getReservation(reservationId: Long): Reservation {
        val reservationEntity = reservationRepository.findByIdOrNull(reservationId) ?: throw ReservationNotFound()

        return Reservation.fromEntity(reservationEntity)
    }

    @Transactional
    override fun getReservationsByUser(viewerId: Long?, userId: Long, pageable: Pageable): Page<ReservationDTO> {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        if (viewerId != userId && userEntity.profile?.showMyReservations != true) throw ReservationPermissionDenied()

        return reservationRepository.findAllByUser(userEntity, pageable).map(ReservationDTO::fromEntity)
    }

//    @Transactional
//    override fun getReservationsByRoom(roomId: Long): List<Reservation> {
//        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()
//
//        return reservationRepository.findAllByRoom(roomEntity).map(Reservation::fromEntity)
//    }

//    @Transactional
//    override fun getReservationsByDate(startDate: LocalDate, endDate: LocalDate): List<Reservation> {
//        val reservations = reservationRepository.findAll().filter { reservation ->
//            startDate < reservation.endDate && endDate > reservation.startDate
//        }
//
//        return reservations.map(Reservation::fromEntity)
//    }

    // 특정 방의 해당 월의 예약 가능한 날짜와 예약 불가능한 날짜를 가져오는 API를 위한 서비스 로직
    @Transactional
    override fun getAvailabilityByMonth(roomId: Long, yearMonth: YearMonth): RoomAvailabilityResponse {
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()

        val reservations = reservationRepository.findAllByRoom(roomEntity)

        val unavailableDates = reservations.flatMap { reservation ->
            reservation.startDate.datesUntil(reservation.endDate).toList()
        }.toSet()

        val filteredUnavailableDates = unavailableDates.filter { date ->
            !date.isBefore(startDate) && !date.isAfter(endDate)
        }.toSet()

        val allDates = startDate.datesUntil(endDate.plusDays(1)).toList()
        val availableDates = allDates.filterNot { it in filteredUnavailableDates }

        return RoomAvailabilityResponse(
            availableDates = availableDates,
            unavailableDates = filteredUnavailableDates.toList()
        )
    }
}
