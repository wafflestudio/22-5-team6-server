package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.image.persistence.ImageEntity
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "rooms",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["street", "detail"]
        )
    ]
)
class RoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host", nullable = false)
    var host: UserEntity,
    @Column(nullable = false)
    var name: String,
    @Column(columnDefinition = "TEXT", nullable = false)
    var description: String,
    @Column(nullable = false)
    var type: RoomType,
    @Embedded
    var address: Address,
    @Embedded
    var ratingStatistics: RatingStatistics = RatingStatistics(),
    @Embedded
    var roomDetails: RoomDetails,
    @Embedded
    var price: Price,
    @Column(nullable = false)
    var maxOccupancy: Int,
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, orphanRemoval = true)
    val reservations: List<ReservationEntity> = mutableListOf(),
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, orphanRemoval = true)
    val reviews: List<ReviewEntity> = mutableListOf(),
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, orphanRemoval = true)
    val images: List<ImageEntity> = mutableListOf(),
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, orphanRemoval = true)
    val likes: List<RoomLikeEntity> = mutableListOf(),
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
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
