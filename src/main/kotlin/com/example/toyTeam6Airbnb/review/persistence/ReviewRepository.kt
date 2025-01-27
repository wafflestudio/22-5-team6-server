package com.example.toyTeam6Airbnb.review.persistence

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface ReviewRepository : JpaRepository<ReviewEntity, Long> {
    fun findAllByRoomId(roomId: Long, pageable: Pageable): Page<ReviewEntity>
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<ReviewEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReviewEntity r WHERE r.id = :id")
    fun findByIdOrNullForUpdate(id: Long): ReviewEntity?
}
