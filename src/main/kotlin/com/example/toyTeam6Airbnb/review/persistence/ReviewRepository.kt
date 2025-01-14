package com.example.toyTeam6Airbnb.review.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<ReviewEntity, Long> {
    fun findAllByRoomId(roomId: Long, pageable: Pageable): Page<ReviewEntity>
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<ReviewEntity>
}
