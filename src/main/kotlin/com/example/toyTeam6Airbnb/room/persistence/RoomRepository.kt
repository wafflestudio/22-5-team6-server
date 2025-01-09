package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface RoomRepository : JpaRepository<RoomEntity, Long>, JpaSpecificationExecutor<RoomEntity> {

    @Query(
        """
        SELECT r FROM RoomEntity r
        LEFT JOIN r.reviews rev
        LEFT JOIN r.address addr
        WHERE (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:type IS NULL OR r.type = :type)
        AND (:minPrice IS NULL OR r.price >= :minPrice)
        AND (:maxPrice IS NULL OR r.price <= :maxPrice)
        AND (:maxOccupancy IS NULL OR r.maxOccupancy >= :maxOccupancy)
        AND (
            :rating IS NULL 
            OR AVG(rev.rating) >= :rating
        )
        AND (
            (:startDate IS NOT NULL AND :endDate IS NOT NULL AND NOT EXISTS (
                SELECT 1 FROM ReservationEntity resInner
                WHERE resInner.room = r
                AND resInner.startDate < :endDate
                AND resInner.endDate > :startDate
            ))
            OR (:startDate IS NULL OR :endDate IS NULL)
        )
        AND (
            :address IS NULL OR (
                (:#{#address.sido} IS NULL OR LOWER(addr.sido) = LOWER(:#{#address.sido}))
                AND (:#{#address.sigungu} IS NULL OR LOWER(addr.sigungu) = LOWER(:#{#address.sigungu}))
                AND (:#{#address.street} IS NULL OR LOWER(addr.street) LIKE LOWER(CONCAT('%', :#{#address.street}, '%')))
                AND (:#{#address.detail} IS NULL OR LOWER(addr.detail) LIKE LOWER(CONCAT('%', :#{#address.detail}, '%')))
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
        @Param("address") address: AddressSearchDTO?,
        @Param("maxOccupancy") maxOccupancy: Int?,
        @Param("rating") rating: Double?,
        @Param("startDate") startDate: LocalDate?,
        @Param("endDate") endDate: LocalDate?,
        pageable: Pageable
    ): Page<RoomEntity>
}
