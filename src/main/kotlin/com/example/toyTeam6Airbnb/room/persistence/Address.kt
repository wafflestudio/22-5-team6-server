package com.example.toyTeam6Airbnb.room.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Address(
    @Column(name = "sid", nullable = false)
    val sido: String,

    @Column(name = "sig", nullable = false)
    val sigungu: String,

    @Column(name = "str", nullable = false)
    val street: String,

    @Column(name = "det", nullable = false)
    val detail: String
)
