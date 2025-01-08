package com.example.toyTeam6Airbnb.review.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<ReviewEntity, Long> {
    fun findAllByRoomId(roomId: Long): List<ReviewEntity>
}
