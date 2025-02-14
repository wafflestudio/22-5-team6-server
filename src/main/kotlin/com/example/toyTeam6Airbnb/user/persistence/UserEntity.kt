package com.example.toyTeam6Airbnb.user.persistence

import com.example.toyTeam6Airbnb.image.persistence.ImageEntity
import com.example.toyTeam6Airbnb.profile.persistence.ProfileEntity
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomLikeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

@Entity(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var username: String,

    @Column(nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING)
    val provider: AuthProvider,

    @Column
    var oAuthId: String? = null,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    var image: ImageEntity? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val reservations: List<ReservationEntity> = mutableListOf(),

    @OneToMany(mappedBy = "host", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val rooms: List<RoomEntity> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val reviews: List<ReviewEntity> = mutableListOf(),

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    var profile: ProfileEntity? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE], fetch = FetchType.LAZY, orphanRemoval = true)
    var refreshTokens: MutableList<RefreshTokenEntity> = mutableListOf(),

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var roomLikes: MutableList<RoomLikeEntity> = mutableListOf()
) {
    fun isSuperHost(): Boolean {
        return profile?.isSuperHost == true
    }
}

enum class AuthProvider {
    LOCAL, GOOGLE, KAKAO, NAVER;

    companion object {
        fun from(provider: String?): AuthProvider {
            return try {
                provider?.uppercase()?.let { valueOf(it) }
                    ?: throw Exception("Provider cannot be null")
            } catch (e: IllegalArgumentException) {
                throw Exception("Invalid provider: $provider")
            }
        }
    }
}
