package com.example.toyTeam6Airbnb.Image.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : JpaRepository<ImageEntity, Long> {
    fun findByRoomId(roomId: Long): List<ImageEntity>
    fun findByUserId(userId: Long): ImageEntity?
}
