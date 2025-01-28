package com.example.toyTeam6Airbnb.reservation.persistence

import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ReservationRepository : JpaRepository<ReservationEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReservationEntity r WHERE r.id = :id")
    fun findByIdOrNullForUpdate(id: Long): ReservationEntity?

    @Query("SELECT r FROM ReservationEntity r WHERE r.startDate <= :endDate AND r.endDate >= :startDate")
    fun findReservationsByDateRange(@Param("startDate") startDate: LocalDate, @Param("endDate") endDate: LocalDate): List<ReservationEntity>

    fun findAllByRoom(room: RoomEntity): List<ReservationEntity>

    fun findAllByUser(user: UserEntity, pageable: Pageable): Page<ReservationEntity>
}
