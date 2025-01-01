package com.example.toyTeam6Airbnb.user.persistence

import com.example.toyTeam6Airbnb.profile.controller.Profile
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import jakarta.persistence.*

@Entity(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    @Column(name = "username", nullable = false)
    var username: String,
    @Column(name = "password", nullable = false)
    var password: String,
    @Column(name = "kakao_id", nullable = false)
    var kakaoId: String,
    @Column(name = "kakao_nickname", nullable = false)
    var kakaoNickname: String,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reservations: List<ReservationEntity> = mutableListOf(),

    @OneToMany(mappedBy = "host", cascade = [CascadeType.ALL], orphanRemoval = true)
    val rooms: List<RoomEntity> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reviews: List<ReviewEntity> = mutableListOf(),

    )
