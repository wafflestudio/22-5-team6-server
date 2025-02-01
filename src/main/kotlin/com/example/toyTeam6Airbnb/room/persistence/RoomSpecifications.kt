package com.example.toyTeam6Airbnb.room.persistence

import com.example.toyTeam6Airbnb.reservation.persistence.ReservationEntity
import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import com.example.toyTeam6Airbnb.room.controller.RoomDetailSearchDTO
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

class RoomSpecifications {

    companion object {

        fun hasName(name: String?): Specification<RoomEntity> = Specification { root, _, cb ->
            if (name.isNullOrBlank()) {
                cb.conjunction()
            } else {
                cb.like(cb.lower(root.get("name")), "%${name.lowercase()}%")
            }
        }

        fun hasType(type: RoomType?): Specification<RoomEntity> = Specification { root, _, cb ->
            type?.let {
                cb.equal(root.get<RoomType>("type"), it)
            } ?: cb.conjunction()
        }

        fun hasPriceBetween(minPrice: Double?, maxPrice: Double?): Specification<RoomEntity> =
            Specification { root, _, cb ->
                val pricePath = root.get<Price>("price").get<Double>("total")
                when {
                    minPrice != null && maxPrice != null -> cb.between(pricePath, minPrice, maxPrice)
                    minPrice != null -> cb.ge(pricePath, minPrice)
                    maxPrice != null -> cb.le(pricePath, maxPrice)
                    else -> cb.conjunction()
                }
            }

        fun hasMaxOccupancy(maxOccupancy: Int?): Specification<RoomEntity> = Specification { root, _, cb ->
            maxOccupancy?.let {
                cb.ge(root.get<Int>("maxOccupancy"), it)
            } ?: cb.conjunction()
        }

        fun isAvailable(startDate: LocalDate?, endDate: LocalDate?): Specification<RoomEntity> =
            Specification { root, query, cb ->
                if (startDate == null || endDate == null) {
                    cb.conjunction()
                } else {
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

        fun hasAddress(address: AddressSearchDTO?): Specification<RoomEntity> = Specification { root, _, cb ->
            address?.takeIf { it.hasAnyField() }?.let {
                val addressJoin = root.join<RoomEntity, Address>("address", JoinType.LEFT)
                val predicates = mutableListOf<Predicate>()

                it.sido?.let { sido ->
                    predicates.add(cb.equal(cb.lower(addressJoin.get<String>("sido")), sido.lowercase()))
                }
                it.sigungu?.let { sigungu ->
                    predicates.add(cb.equal(cb.lower(addressJoin.get<String>("sigungu")), sigungu.lowercase()))
                }
                it.street?.let { street ->
                    predicates.add(cb.like(cb.lower(addressJoin.get<String>("street")), "%${street.lowercase()}%"))
                }
                it.detail?.let { detail ->
                    predicates.add(cb.like(cb.lower(addressJoin.get<String>("detail")), "%${detail.lowercase()}%"))
                }

                if (predicates.isNotEmpty()) cb.and(*predicates.toTypedArray()) else cb.conjunction()
            } ?: cb.conjunction()
        }

        fun hasRoomDetails(roomDetails: RoomDetailSearchDTO?): Specification<RoomEntity> = Specification { root, _, cb ->
            roomDetails?.takeIf { it.hasAnyField() }?.let {
                val detailsJoin = root.join<RoomEntity, RoomDetails>("roomDetails", JoinType.LEFT)
                val predicates = mutableListOf<Predicate>()

                it.wifi?.let { wifi ->
                    predicates.add(cb.equal(detailsJoin.get<Boolean>("wifi"), wifi))
                }
                it.selfCheckin?.let { selfCheckin ->
                    predicates.add(cb.equal(detailsJoin.get<Boolean>("selfCheckin"), selfCheckin))
                }
                it.luggage?.let { luggage ->
                    predicates.add(cb.equal(detailsJoin.get<Boolean>("luggage"), luggage))
                }
                it.tv?.let { tv ->
                    predicates.add(cb.equal(detailsJoin.get<Boolean>("tv"), tv))
                }
                it.bedRoom?.let { bedRoom ->
                    predicates.add(cb.ge(detailsJoin.get<Int>("bedroom"), bedRoom))
                }
                it.bathRoom?.let { bathRoom ->
                    predicates.add(cb.ge(detailsJoin.get<Int>("bathroom"), bathRoom))
                }
                it.bed?.let { bed ->
                    predicates.add(cb.ge(detailsJoin.get<Int>("bed"), bed))
                }

                if (predicates.isNotEmpty()) cb.and(*predicates.toTypedArray()) else cb.conjunction()
            } ?: cb.conjunction()
        }

        fun hasRating(rating: Double?): Specification<RoomEntity> = Specification { root, _, cb ->
            val ratingPath = root.get<RatingStatistics>("ratingStatistics").get<Double>("averageRating")
            when {
                rating != null -> cb.ge(ratingPath, rating)
                else -> cb.conjunction()
            }
        }

        private fun AddressSearchDTO.hasAnyField(): Boolean =
            listOf(sido, sigungu, street, detail).any { it != null }

        private fun RoomDetailSearchDTO.hasAnyField(): Boolean =
            listOf(wifi, selfCheckin, luggage, tv, bedRoom, bathRoom, bed).any { it != null }
    }
}
