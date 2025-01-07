package com.example.toyTeam6Airbnb.review.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface ReviewRepository : JpaRepository<ReviewEntity, Long>{
    fun findAllByRoomId(roomId: Long): List<ReviewEntity>
}