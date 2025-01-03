package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Table
import jakarta.persistence.OneToMany
import jakarta.persistence.GenerationType
import java.time.Instant

@Entity
@Table(name = "rooms")
class RoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    var hostId: Long,
    @Column(nullable = false)
    val name: String,
    @Column(columnDefinition = "TEXT")
    var description: String?,
    @Column
    val type: String?,
    @Column(columnDefinition = "TEXT", nullable = false)
    var address: String,
    @Column(nullable = false)
    val price: Double,
    @Column(nullable = false)
    val maxOccupancy: Int,
    @Column(columnDefinition = "TEXT")
    var facilities: String?,
    @Column(columnDefinition = "TEXT")
    var interaction: String?,
    @OneToMany(mappedBy = "room")
    var reservations: List<ReservationEntity> = emptyList(),
    @OneToMany(mappedBy = "room")
    var reviews: List<ReviewEntity> = emptyList(),
    @Column(nullable = false)
    var createdAt: Instant,
    @Column(nullable = false)
    var updatedAt: Instant,
)