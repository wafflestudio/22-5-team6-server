package com.example.toyTeam6Airbnb.profile.persistence

import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "profiles")
class ProfileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: UserEntity,

    val personalInfo: String? = null,
    val hostedAccommodations: String? = null
)
