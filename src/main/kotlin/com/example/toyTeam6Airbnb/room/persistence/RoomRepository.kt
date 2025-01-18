package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface RoomRepository : JpaRepository<RoomEntity, Long>, JpaSpecificationExecutor<RoomEntity> {
    fun existsByAddress(address: Address): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // query for find by id with lock
    @Query("SELECT r FROM RoomEntity r WHERE r.id = :id")
    fun findByIdOrNullForUpdate(id: Long): RoomEntity?

    fun countByHost(userEntity: UserEntity): Int
}
