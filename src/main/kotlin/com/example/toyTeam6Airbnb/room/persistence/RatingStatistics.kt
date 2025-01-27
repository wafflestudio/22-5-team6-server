package com.example.toyTeam6Airbnb.room.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class RatingStatistics(
    @Column(name = "average_rating")
    var averageRating: Double = 0.0,

    @Column(name = "rating1")
    var rating1: Int = 0,

    @Column(name = "rating2")
    var rating2: Int = 0,

    @Column(name = "rating3")
    var rating3: Int = 0,

    @Column(name = "rating4")
    var rating4: Int = 0,

    @Column(name = "rating5")
    var rating5: Int = 0
) {
    fun incrementRating(rating: Int) {
        when (rating) {
            1 -> rating1++
            2 -> rating2++
            3 -> rating3++
            4 -> rating4++
            5 -> rating5++
            else -> throw IllegalArgumentException("Invalid rating: $rating")
        }
        updateAverageRating()
    }
    fun decrementRating(rating: Int) {
        when (rating) {
            1 -> if (rating1 > 0) rating1--
            2 -> if (rating2 > 0) rating2--
            3 -> if (rating3 > 0) rating3--
            4 -> if (rating4 > 0) rating4--
            5 -> if (rating5 > 0) rating5--
            else -> throw IllegalArgumentException("Invalid rating: $rating")
        }
        updateAverageRating()
    }
    fun updateAverageRating() {
        val totalRating = rating1 + rating2 + rating3 + rating4 + rating5
        averageRating = if (totalRating == 0) {
            0.0
        } else {
            (rating1 + rating2 * 2 + rating3 * 3 + rating4 * 4 + rating5 * 5).toDouble() / totalRating
        }
    }
}
