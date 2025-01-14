package com.example.toyTeam6Airbnb.reservation.persistence

import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface ReservationRepository : JpaRepository<ReservationEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReservationEntity r WHERE r.id = :id")
    fun findByIdOrNullForUpdate(id: Long): ReservationEntity?

    fun findAllByRoom(room: RoomEntity): List<ReservationEntity>

    fun findAllByUser(user: UserEntity, pageable: Pageable): Page<ReservationEntity>
}
