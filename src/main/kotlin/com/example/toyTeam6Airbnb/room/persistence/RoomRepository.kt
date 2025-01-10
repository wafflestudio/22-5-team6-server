package com.example.toyTeam6Airbnb.room.persistence

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface RoomRepository : JpaRepository<RoomEntity, Long>, JpaSpecificationExecutor<RoomEntity> {
    fun existsByAddress(address: Address): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // query for find by id with lock
    @Query("SELECT r FROM RoomEntity r WHERE r.id = :id")
    fun findByIdOrNullForUpdate(id: Long): RoomEntity?

    @Query(
        """
        SELECT r FROM RoomEntity r
        LEFT JOIN r.reviews rev
        LEFT JOIN r.address addr
        WHERE 
            (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:type IS NULL OR r.type = :type)
            AND (:minPrice IS NULL OR r.price.total >= :minPrice)
            AND (:maxPrice IS NULL OR r.price.total <= :maxPrice)
            AND (:maxOccupancy IS NULL OR r.maxOccupancy >= :maxOccupancy)
            AND (
                (:startDate IS NULL OR :endDate IS NULL) OR
                NOT EXISTS (
                    SELECT 1 FROM ReservationEntity res
                    WHERE res.room = r
                    AND res.startDate < :endDate
                    AND res.endDate > :startDate
                )
            )
            AND (
                (:sido IS NULL AND :sigungu IS NULL AND :street IS NULL AND :detail IS NULL) OR (
                    (:sido IS NULL OR LOWER(addr.sido) = LOWER(:sido))
                    AND (:sigungu IS NULL OR LOWER(addr.sigungu) = LOWER(:sigungu))
                    AND (:street IS NULL OR LOWER(addr.street) LIKE LOWER(CONCAT('%', :street, '%')))
                    AND (:detail IS NULL OR LOWER(addr.detail) LIKE LOWER(CONCAT('%', :detail, '%')))
                )
            )
        GROUP BY r
        HAVING (:rating IS NULL OR AVG(rev.rating) >= :rating)
        """
    )
    fun searchAvailableRooms(
        @Param("name") name: String?,
        @Param("type") type: RoomType?,
        @Param("minPrice") minPrice: Double?,
        @Param("maxPrice") maxPrice: Double?,
        @Param("maxOccupancy") maxOccupancy: Int?,
        @Param("rating") rating: Double?,
        @Param("startDate") startDate: LocalDate?,
        @Param("endDate") endDate: LocalDate?,
        @Param("sido") sido: String?,
        @Param("sigungu") sigungu: String?,
        @Param("street") street: String?,
        @Param("detail") detail: String?,
        pageable: Pageable
    ): Page<RoomEntity>
}
