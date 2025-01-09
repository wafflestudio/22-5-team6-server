package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.profile.persistence.ProfileRepository
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.JwtTokenProvider
import com.example.toyTeam6Airbnb.user.persistence.AuthProvider
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DataGenerator(
    private val profileRepository: ProfileRepository,
    private val reservationRepository: ReservationRepository,
    private val reviewRepository: ReviewRepository,
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun clearAll() {
        profileRepository.deleteAll()
        reservationRepository.deleteAll()
        reviewRepository.deleteAll()
        roomRepository.deleteAll()
        userRepository.deleteAll()
    }

    fun generateUserAndToken(
        username: String? = null,
        provider: AuthProvider = AuthProvider.LOCAL
    ): Pair<UserEntity, String> {
        val userEntity = userRepository.save(
            UserEntity(
                username = username ?: "user-${(0..10000).random()}",
                password = "",
                provider = provider
            )
        )
        return userEntity to jwtTokenProvider.generateToken(userEntity.username)
    }

    fun generateReview(
        reservation: ReservationEntity? = null,
        content: String? = null,
        rating: Int? = null
    ): ReviewEntity {
        val reservationEntity = reservation ?: generateReservation()
        return reviewRepository.save(
            ReviewEntity(
                user = reservationEntity.user,
                reservation = reservationEntity,
                room = reservationEntity.room,
                content = content ?: "content-${(0..10000).random()}",
                rating = rating ?: (1..5).random()
            )
        )
    }

    fun generateReservation(
        user: UserEntity? = null,
        room: RoomEntity? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        totalPrice: Double? = null,
        numberOfGuests: Int? = null
    ): ReservationEntity {
        val userEntity = user ?: generateUserAndToken().first
        val roomEntity = room ?: generateRoom()
        return reservationRepository.save(
            ReservationEntity(
                user = userEntity,
                room = roomEntity,
                startDate = startDate ?: LocalDate.now(),
                endDate = endDate ?: LocalDate.now().plusDays(1),
                totalPrice = totalPrice ?: (10000..100000).random().toDouble(),
                numberOfGuests = numberOfGuests ?: (1..10).random(),
                review = null
            )
        )
    }

    fun generateRoom(
        host: UserEntity? = null,
        name: String? = null,
        description: String? = null,
        type: String? = null,
        address: String? = null,
        price: Double? = null,
        maxOccupancy: Int? = null
    ): RoomEntity {
        val hostEntity = host ?: generateUserAndToken().first
        return roomRepository.save(
            RoomEntity(
                host = hostEntity,
                name = name ?: "room-${(0..10000).random()}",
                description = description ?: "description-${(0..10000).random()}",
                type = type ?: "APARTMENT",
                address = address ?: "",
                price = price ?: (10000..100000).random().toDouble(),
                maxOccupancy = maxOccupancy ?: (1..10).random()
            )
        )
    }
}
