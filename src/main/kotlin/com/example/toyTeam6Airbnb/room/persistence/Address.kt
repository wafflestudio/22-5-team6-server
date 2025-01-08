package com.example.toyTeam6Airbnb.room.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotBlank

@Embeddable
data class Address(
    @field:NotBlank(message = "Country is mandatory")
    @Column(name = "country", nullable = false)
    val country: String, // 국가

    @field:NotBlank(message = "City or Province is mandatory")
    @Column(name = "city_or_province", nullable = false)
    val cityOrProvince: String, // 시, 도

    @field:NotBlank(message = "District or County is mandatory")
    @Column(name = "district_or_county", nullable = false)
    val districtOrCounty: String, // 구, 군

    @field:NotBlank(message = "Neighborhood or Town is mandatory")
    @Column(name = "neighborhood_or_town", nullable = false)
    val neighborhoodOrTown: String // 동, 읍, 면
)
