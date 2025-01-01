package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.*
@Entity
@Table(name = "rooms")
class RoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    val host: UserEntity,

    @Column(nullable = false)
    val name: String,

    @OneToMany(mappedBy = "room", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reservations: List<ReservationEntity> = mutableListOf(),

    @OneToMany(mappedBy = "room", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reviews: List<ReviewEntity> = mutableListOf(),

    val description: String? = null,
    val type: String? = null,
    val address: String? = null,
    val price: Double,
    val maxOccupancy: Int,
    val facilities: String? = null,
    val interaction: String? = null
)
