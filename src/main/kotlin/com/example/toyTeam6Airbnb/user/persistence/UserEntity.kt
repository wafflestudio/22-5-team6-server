package com.example.toyTeam6Airbnb.user.persistence

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var username: String,

    @Column
    var password: String? = null,

    @Enumerated(EnumType.STRING)
    val provider: AuthProvider,
    @Column
    var oauthId: String? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reservations: List<ReservationEntity> = mutableListOf(),

    @OneToMany(mappedBy = "host", cascade = [CascadeType.ALL], orphanRemoval = true)
    val rooms: List<RoomEntity> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reviews: List<ReviewEntity> = mutableListOf()

)

enum class AuthProvider {
    LOCAL, GOOGLE, KAKAO, NAVER
}