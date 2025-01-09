package com.example.toyTeam6Airbnb

import com.example.toyTeam6Airbnb.profile.persistence.ProfileRepository
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewRepository
import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import com.example.toyTeam6Airbnb.user.JwtTokenProvider
import com.example.toyTeam6Airbnb.user.persistence.AuthProvider
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
                review = null,
                startDate = startDate ?: LocalDate.now().plusDays((1..100).random().toLong()),
                endDate = endDate ?: startDate!!.plusDays((1..10).random().toLong()),
                totalPrice = roomEntity.price * ChronoUnit.DAYS.between(startDate, endDate),
                numberOfGuests = numberOfGuests ?: (1..10).random()
            )
        )
    }

    fun generateRoom(
        host: UserEntity? = null,
        name: String? = null,
        description: String? = null,
        type: RoomType? = null,
        address: Address? = null,
        roomDetails: RoomDetails? = null,
        price: Double? = null,
        maxOccupancy: Int? = null
    ): RoomEntity {
        val hostEntity = host ?: generateUserAndToken().first
        val randomType = RoomType.entries.toTypedArray().random()
        return roomRepository.save(
            RoomEntity(
                host = hostEntity,
                name = name ?: "room-${(0..10000).random()}",
                description = description ?: "description-${(0..10000).random()}",
                type = type ?: randomType,
                address = address ?: Address(
                    sido = "sido-${(0..10000).random()}",
                    sigungu = "sigungu-${(0..10000).random()}",
                    street = "street-${(0..10000).random()}",
                    detail = "detail-${(0..10000).random()}"
                ),
                roomDetails = roomDetails ?: RoomDetails(
                    wifi = (0..1).random() == 1,
                    selfCheckin = (0..1).random() == 1,
                    luggage = (0..1).random() == 1,
                    TV = (0..1).random() == 1
                ),
                price = price ?: (10000..100000).random().toDouble(),
                maxOccupancy = maxOccupancy ?: (1..10).random()
            )
        )
    }
}
