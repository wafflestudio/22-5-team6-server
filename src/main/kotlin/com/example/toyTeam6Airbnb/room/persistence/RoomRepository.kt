package com.example.toyTeam6Airbnb.room.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface RoomRepository : JpaRepository<RoomEntity, Long>, JpaSpecificationExecutor<RoomEntity>
