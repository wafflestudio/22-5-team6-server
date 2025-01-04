package com.example.toyTeam6Airbnb.room.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Page

interface RoomRepository : JpaRepository<RoomEntity, Long>{
    override fun findAll(pageable: Pageable): Page<RoomEntity>
}
