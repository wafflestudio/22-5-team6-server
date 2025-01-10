package com.example.toyTeam6Airbnb.room.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class RoomDetails(
    @Column(name = "wifi", nullable = false)
    val wifi: Boolean,

    @Column(name = "selfCheckin", nullable = false)
    val selfCheckin: Boolean,

    @Column(name = "luggage", nullable = false)
    val luggage: Boolean,

    @Column(name = "TV", nullable = false)
    val TV: Boolean,

    @Column(name = "bedroom", nullable = false)
    val bedroom: Int,

    @Column(name = "bathroom", nullable = false)
    val bathroom: Int,

    @Column(name = "bed", nullable = false)
    val bed: Int
)
