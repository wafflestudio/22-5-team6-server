package com.example.toyTeam6Airbnb.reservation.controller

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.room.persistence.Price
import java.time.LocalDate

data class Reservation(
    val reservationId: Long
) {
    companion object {
        fun fromEntity(entity: ReservationEntity): Reservation {
            return Reservation(
                reservationId = entity.id!!
            )
        }
    }
}

data class ReservationDetails(
    val reservationId: Long,
    val roomId: Long,
    val roomName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val place: String,
    val price: Double,
    val numberOfGuests: Int,
    val imageUrl: String // 대표이미지 다운로드 URL
) {
    companion object {
        fun fromEntity(entity: ReservationEntity, imageUrl: String): ReservationDetails {
            return ReservationDetails(
                reservationId = entity.id!!,
                roomId = entity.room.id!!,
                roomName = entity.room.name,
                startDate = entity.startDate,
                endDate = entity.endDate,
                place = entity.room.address.sido,
                price = entity.room.price.total, // 숙소 가격
                numberOfGuests = entity.numberOfGuests,
                imageUrl = imageUrl // 대표이미지 다운로드 URL
            )
        }
    }
}

data class ReservationDTO(
    val reservationId: Long,
    val place: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val imageUrl: String // 대표 이미지 다운로드 URL
) {
    companion object {
        fun fromEntity(entity: ReservationEntity, imageUrl: String): ReservationDTO {
            return ReservationDTO(
                reservationId = entity.id!!,
                place = entity.room.address.sigungu,
                startDate = entity.startDate,
                endDate = entity.endDate,
                imageUrl = imageUrl // 대표이미지 다운로드 url, 이렇게하는 이유가 Reservation entity에 연결된 Room은 Entity인데 이거에 연결되있는건 Image엔티티들이라 Url을 가져오기 위해 service를 또 불러야만함
                // 이걸 해결하고싶으면 이미지 엔티티안에 생성할때마다 다운로드 Url, 업로드 Url을 저장해둬서 하는게 더 간편한 로직이 생성됨.
            )
        }
    }
}
