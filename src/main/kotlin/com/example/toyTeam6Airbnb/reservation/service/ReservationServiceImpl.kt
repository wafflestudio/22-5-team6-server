package com.example.toyTeam6Airbnb.reservation.service

import com.example.toyTeam6Airbnb.image.service.ImageService
import com.example.toyTeam6Airbnb.reservation.MaxOccupancyExceeded
import com.example.toyTeam6Airbnb.reservation.ReservationNotFound
import com.example.toyTeam6Airbnb.reservation.ReservationPermissionDenied
import com.example.toyTeam6Airbnb.reservation.ReservationUnavailable
import com.example.toyTeam6Airbnb.reservation.ZeroGuests
import com.example.toyTeam6Airbnb.reservation.controller.Reservation
import com.example.toyTeam6Airbnb.reservation.controller.ReservationDTO
import com.example.toyTeam6Airbnb.reservation.controller.ReservationDetails
import com.example.toyTeam6Airbnb.reservation.controller.RoomAvailabilityResponse
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.UserNotFoundException
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import com.example.toyTeam6Airbnb.validatePageableForReservation
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.springframework.context.ApplicationContext
import org.springframework.dao.DataIntegrityViolationException
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
    private val entityManager: EntityManager,
    private val imageService: ImageService,
    private val applicationContext: ApplicationContext
) : ReservationService {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun createReservation(
        user: User,
        roomId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        numberOfGuests: Int
    ): Reservation {
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw UserNotFoundException()

        val roomEntity = roomRepository.findByIdOrNullForUpdate(roomId) ?: throw RoomNotFoundException()

        if (!isAvailable(roomEntity, startDate, endDate)) throw ReservationUnavailable()

        if (numberOfGuests > roomEntity.maxOccupancy) throw MaxOccupancyExceeded()
        if (numberOfGuests == 0) throw ZeroGuests()

        val reservationEntity = ReservationEntity(
            user = userEntity,
            room = roomEntity,
            review = null,
            startDate = startDate,
            endDate = endDate,
            totalPrice = roomEntity.price.total * ChronoUnit.DAYS.between(startDate, endDate),
            numberOfGuests = numberOfGuests
        )

        try {
            reservationRepository.save(reservationEntity)
        } catch (e: DataIntegrityViolationException) {
            throw ReservationUnavailable()
        }

        return Reservation.fromEntity(reservationEntity)
    }

    fun isAvailable(room: RoomEntity, startDate: LocalDate, endDate: LocalDate, currentReservationId: Long? = null): Boolean {
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
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw UserNotFoundException()
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
        val userEntity = userRepository.findByIdOrNull(user.id) ?: throw UserNotFoundException()
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
    override fun getReservation(
        userId: Long,
        reservationId: Long
    ): ReservationDetails {
        val reservationEntity = reservationRepository.findByIdOrNull(reservationId) ?: throw ReservationNotFound()
        if (reservationEntity.user.id != userId) throw ReservationPermissionDenied()

        val imageUrl = imageService.generateRoomImageDownloadUrl(reservationEntity.room.id!!)
        return ReservationDetails.fromEntity(reservationEntity, imageUrl)
    }

    @Transactional
    override fun getReservationsByUser(viewerId: Long?, userId: Long, pageable: Pageable): Page<ReservationDTO> {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        if (viewerId != userId && userEntity.profile?.showMyReservations != true) throw ReservationPermissionDenied()

        return reservationRepository.findAllByUser(userEntity, validatePageableForReservation(pageable)).map { reservation ->
            val imageUrl = imageService.generateRoomImageDownloadUrl(reservation.room.id!!)
            ReservationDTO.fromEntity(reservation, imageUrl)
        }
    }

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
