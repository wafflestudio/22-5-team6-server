package com.example.toyTeam6Airbnb.room.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Price(
    @Column(name = "perNight", nullable = false)
    val perNight: Double,

    @Column(name = "clean", nullable = false)
    val cleaningFee: Double,

    @Column(name = "charge", nullable = false)
    val charge: Double,

    @Column(name = "total", nullable = false)
    var total: Double = perNight + cleaningFee + charge
) {
    fun updateTotal(): Double {
        total = perNight + cleaningFee + charge
        return total
    }
}
