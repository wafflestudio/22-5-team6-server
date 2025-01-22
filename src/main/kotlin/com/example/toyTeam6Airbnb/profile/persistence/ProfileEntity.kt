package com.example.toyTeam6Airbnb.profile.persistence

import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "profiles")
class ProfileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: UserEntity,

    @Column(nullable = false)
    var nickname: String,

    @Column(nullable = false)
    var bio: String,

    @Column(nullable = false)
    var isSuperHost: Boolean = false,

    @Column(nullable = false)
    var showMyReviews: Boolean = false,

    @Column(nullable = false)
    var showMyReservations: Boolean = false,

    @Column(nullable = false)
    var showMyWishlist: Boolean = false
)
