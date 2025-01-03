package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import jakarta.persistence.*
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
    var name: String,
    @Column(columnDefinition = "TEXT")
    var description: String?,
    @Column
    var type: String?,
    @Column(columnDefinition = "TEXT", nullable = false)
    var address: String,
    @Column(nullable = false)
    var price: Double,
    @Column(nullable = false)
    var maxOccupancy: Int,
    @Column(columnDefinition = "TEXT")
    var facilities: String?,
    @Column(columnDefinition = "TEXT")
    var interaction: String?,
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    val reservations: List<ReservationEntity> = mutableListOf(),
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    val reviews: List<ReviewEntity> = mutableListOf(),
    @Column(nullable = false)
    var createdAt: Instant,
    @Column(nullable = false)
    var updatedAt: Instant,
)