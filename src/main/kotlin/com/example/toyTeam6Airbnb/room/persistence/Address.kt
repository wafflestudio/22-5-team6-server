package com.example.toyTeam6Airbnb.room.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Address(
    @Column(name = "country", nullable = false)
    val country: String,

    @Column(name = "city_or_province", nullable = false)
    val cityOrProvince: String,

    @Column(name = "district_or_county", nullable = false)
    val districtOrCounty: String,

    @Column(name = "neighborhood_or_town", nullable = false)
    val neighborhoodOrTown: String
)
