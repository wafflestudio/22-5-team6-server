package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RoomRepository : JpaRepository<RoomEntity, Long>, JpaSpecificationExecutor<RoomEntity> {
    fun existsByAddress(address: Address): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RoomEntity r WHERE r.id = :id")
    fun findByIdOrNullForUpdate(id: Long): RoomEntity?

    fun countByHost(userEntity: UserEntity): Int

    @Query("SELECT r FROM RoomEntity r WHERE r.host = :host")
    fun findAllByHostId(hostId: Long, pageable: Pageable): Page<RoomEntity>

    @Query("SELECT r FROM RoomEntity r WHERE r.address.sido = :sido and r.address.sigungu = :sigungu ORDER BY r.ratingStatistics.averageRating DESC")
    fun findTopRoomsByArea(
        @Param("sido") sido: String,
        @Param("sigungu") sigungu: String,
        pageable: Pageable
    ): Page<RoomEntity>
}
