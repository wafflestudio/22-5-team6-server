package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RoomLikeRepository : JpaRepository<RoomLikeEntity, Long> {
    @Query("SELECT rl.room FROM RoomLikeEntity rl WHERE rl.user.id = :userId")
    fun findRoomsLikedByUser(@Param("user") user: UserEntity, pageable: Pageable): Page<RoomEntity>

    fun findByUserIdAndRoomIdIn(userId: Long, roomIds: List<Long>): List<RoomLikeEntity>

    fun existsByUserIdAndRoomId(userId: Long, roomId: Long): Boolean
}
