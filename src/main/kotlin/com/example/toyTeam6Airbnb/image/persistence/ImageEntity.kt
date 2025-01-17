package com.example.toyTeam6Airbnb.image.persistence

import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne

@Entity
class ImageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "room_id")
    val room: RoomEntity? = null,

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: UserEntity? = null
)
