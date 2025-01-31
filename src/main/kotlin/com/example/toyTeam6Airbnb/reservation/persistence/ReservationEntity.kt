package com.example.toyTeam6Airbnb.reservation.persistence

import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(
    name = "reservations",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["room_id", "startDate", "endDate"])
    ]
)
class ReservationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: RoomEntity,

    @OneToOne(mappedBy = "reservation", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val review: ReviewEntity?,

    @Column(name = "startDate", nullable = false)
    var startDate: LocalDate,

    @Column(name = "endDate", nullable = false)
    var endDate: LocalDate,

    @Column(nullable = false)
    var totalPrice: Double,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(nullable = false)
    var numberOfGuests: Int

) {
    @PrePersist
    fun onPrePersist() {
        createdAt = Instant.now()
        updatedAt = Instant.now()
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = Instant.now()
    }
}
