package com.example.toyTeam6Airbnb.room.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Address(
    @Column(name = "sido", nullable = false)
    val sido: String,

    @Column(name = "sigungu", nullable = false)
    val sigungu: String,

    @Column(name = "street", nullable = false)
    val street: String,

    @Column(name = "detail", nullable = false)
    val detail: String
)
