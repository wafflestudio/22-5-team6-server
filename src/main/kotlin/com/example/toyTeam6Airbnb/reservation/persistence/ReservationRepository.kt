package com.example.toyTeam6Airbnb.reservation.persistence

import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationRepository : JpaRepository<ReservationEntity, Long> {
    fun findAllByRoom(room: RoomEntity): List<ReservationEntity>

    fun findAllByUser(user: UserEntity): List<ReservationEntity>
}
