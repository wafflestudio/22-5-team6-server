package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.review.persistence.ReviewEntity
import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import com.example.toyTeam6Airbnb.room.controller.RoomDetailSearchDTO
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

class RoomSpecifications {

    companion object {
        fun hasName(name: String?): Specification<RoomEntity> {
            return Specification { root, _, cb ->
                if (name.isNullOrBlank()) {
                    cb.conjunction()
                } else {
                    cb.like(cb.lower(root.get("name")), "%${name.lowercase()}%")
                }
            }
        }

        fun hasType(type: RoomType?): Specification<RoomEntity> {
            return Specification { root, _, cb ->
                if (type == null) {
                    cb.conjunction()
                } else {
                    cb.equal(root.get<RoomType>("type"), type)
                }
            }
        }

        fun hasPriceBetween(minPrice: Double?, maxPrice: Double?): Specification<RoomEntity> {
            return Specification { root, _, cb ->
                when {
                    // minPrice와 maxPrice가 모두 제공된 경우
                    minPrice != null && maxPrice != null -> cb.between(
                        root.get<Double>("price").get<Double>("total"),
                        minPrice,
                        maxPrice
                    )

                    // minPrice만 제공된 경우
                    minPrice != null -> cb.ge(
                        root.get<Double>("price").get<Double>("total"),
                        minPrice
                    )

                    // maxPrice만 제공된 경우
                    maxPrice != null -> cb.le(
                        root.get<Double>("price").get<Double>("total"),
                        maxPrice
                    )

                    // 둘 다 제공되지 않은 경우
                    else -> cb.conjunction()
                }
            }
        }

        fun hasMaxOccupancy(maxOccupancy: Int?): Specification<RoomEntity> {
            return Specification { root, _, cb ->
                if (maxOccupancy == null) {
                    cb.conjunction()
                } else {
                    cb.ge(root.get<Int>("maxOccupancy"), maxOccupancy)
                }
            }
        }

        fun isAvailable(startDate: LocalDate?, endDate: LocalDate?): Specification<RoomEntity> {
            return Specification { root, query, cb ->
                when {
                    startDate == null || endDate == null -> {
                        cb.conjunction()
                    }
                    else -> {
                        val subquery = query!!.subquery(Long::class.java)
                        val reservation = subquery.from(ReservationEntity::class.java)
                        subquery.select(cb.literal(1))
                            .where(
                                cb.equal(reservation.get<RoomEntity>("room"), root),
                                cb.lessThan(reservation.get<LocalDate>("startDate"), endDate),
                                cb.greaterThan(reservation.get<LocalDate>("endDate"), startDate)
                            )
                        cb.not(cb.exists(subquery))
                    }
                }
            }
        }


        fun hasAddress(address: AddressSearchDTO?): Specification<RoomEntity> {
            return Specification { root, _, cb ->
                if (address == null) cb.conjunction()
                else {
                    if (address.sido == null && address.sigungu == null && address.street == null && address.detail == null) {
                        cb.conjunction()
                    } else {
                        val addressJoin =
                            root.join<RoomEntity, Address>("address", jakarta.persistence.criteria.JoinType.LEFT)
                        val predicates = mutableListOf<Predicate>()
                        if (address.sido != null) {
                            predicates.add(cb.equal(cb.lower(addressJoin.get<String>("sido")), address.sido.lowercase()))
                        }
                        if (address.sigungu != null) {
                            predicates.add(cb.equal(cb.lower(addressJoin.get<String>("sigungu")), address.sigungu.lowercase()))
                        }
                        if (address.street != null) {
                            predicates.add(
                                cb.like(
                                    cb.lower(addressJoin.get<String>("street")),
                                    "%${address.street.lowercase()}%"
                                )
                            )
                        }
                        if (address.detail != null) {
                            predicates.add(
                                cb.like(
                                    cb.lower(addressJoin.get<String>("detail")),
                                    "%${address.detail.lowercase()}%"
                                )
                            )
                        }
                        cb.and(*predicates.toTypedArray())
                    }
                }
            }
        }

        fun hasRoomDetails(roomDetails: RoomDetailSearchDTO?): Specification<RoomEntity> {
            return Specification { root, _, cb ->
                if (roomDetails == null) cb.conjunction()
                else {
                    val roomDetailsJoin = root.join<RoomEntity, RoomDetails>("roomDetails", jakarta.persistence.criteria.JoinType.LEFT)
                    val predicates = mutableListOf<Predicate>()
                    if (roomDetails.wifi != null) {
                        predicates.add(cb.equal(roomDetailsJoin.get<Boolean>("wifi"), roomDetails.wifi))
                    }
                    if (roomDetails.selfCheckin != null) {
                        predicates.add(cb.equal(roomDetailsJoin.get<Boolean>("selfCheckin"), roomDetails.selfCheckin))
                    }
                    if (roomDetails.luggage != null) {
                        predicates.add(cb.equal(roomDetailsJoin.get<Boolean>("luggage"), roomDetails.luggage))
                    }
                    if (roomDetails.tv != null) {
                        predicates.add(cb.equal(roomDetailsJoin.get<Boolean>("tv"), roomDetails.tv))
                    }
                    if (roomDetails.bedRoom != null) {
                        predicates.add(cb.ge(roomDetailsJoin.get<Int>("bedRoom"), roomDetails.bedRoom))
                    }
                    if (roomDetails.bathRoom != null) {
                        predicates.add(cb.ge(roomDetailsJoin.get<Int>("bathRoom"), roomDetails.bathRoom))
                    }
                    if (roomDetails.bed != null) {
                        predicates.add(cb.ge(roomDetailsJoin.get<Int>("bed"), roomDetails.bed))
                    }
                    cb.and(*predicates.toTypedArray())
                }
            }
        }

        fun hasRating(rating: Double?): Specification<RoomEntity> {
            return Specification { root, query, cb ->
                if (rating == null) {
                    cb.conjunction()
                } else {
                    // 그룹핑과 HAVING 절을 처리하기 위해 쿼리 루트에 그룹핑 추가
                    root.join<RoomEntity, ReviewEntity>("reviews", jakarta.persistence.criteria.JoinType.LEFT)
                    query!!.groupBy(root.get<Long>("id"))
                    cb.ge(cb.avg(root.get<ReviewEntity>("reviews").get<Double>("rating")), rating)
                }
            }
        }
    }
}
