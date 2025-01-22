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
    // 특정 유저가 좋아요한 방 목록 조회
    @Query("SELECT rl.room FROM RoomLikeEntity rl WHERE rl.user.id = :userId")
    fun findRoomsLikedByUser(@Param("user") user: UserEntity, pageable: Pageable): Page<RoomEntity>
}
