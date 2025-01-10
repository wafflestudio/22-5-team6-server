package com.example.toyTeam6Airbnb.room.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Address(
    @Column(name = "sido", nullable = false, length = 50)
    val sido: String,

    @Column(name = "sigungu", nullable = false, length = 100)
    val sigungu: String,

    @Column(name = "street", nullable = false, length = 100)
    val street: String,

    @Column(name = "detail", nullable = false, length = 100)
    val detail: String
)
