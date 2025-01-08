package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.RoomPermissionDeniedException
import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.criteria.Predicate


@Service
class RoomServiceImpl(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository
) : RoomService {

    @Transactional
    override fun createRoom(
        host: User,
        name: String,
        description: String,
        type: String,
        address: Address,
        price: Double,
        maxOccupancy: Int
    ): Room {
        val hostEntity = userRepository.findByIdOrNull(host.id) ?: throw AuthenticateException()

        val roomEntity =
            RoomEntity(
                host = hostEntity,
                name = name,
                description = description,
                type = type,
                address = address,
                price = price,
                maxOccupancy = maxOccupancy,
                reservations = emptyList(),
                rating = 0.0,
                reviews = emptyList(),
            ).let {
                roomRepository.save(it)
            }

        return Room.fromEntity(roomEntity)
    }

    @Transactional
    override fun getRooms(pageable: Pageable): Page<Room> {
        return roomRepository.findAll(pageable).map { Room.fromEntity(it) }
    }

    @Transactional
    override fun getRoomDetails(roomId: Long): Room {
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()
        return Room.fromEntity(roomEntity)
    }

    @Transactional
    override fun updateRoom(
        host: User,
        roomId: Long,
        name: String?,
        description: String?,
        type: String?,
        address: Address?,
        price: Double?,
        maxOccupancy: Int?
    ): Room {
        val hostEntity = userRepository.findByIdOrNull(host.id) ?: throw AuthenticateException()
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()

        if (roomEntity.host.id != hostEntity.id) {
            throw RoomPermissionDeniedException()
        }

        name?.let { roomEntity.name = it }
        description?.let { roomEntity.description = it }
        type?.let { roomEntity.type = it }
        address?.let { roomEntity.address = it }
        price?.let { roomEntity.price = it }
        maxOccupancy?.let { roomEntity.maxOccupancy = it }

        roomRepository.save(roomEntity)
        return Room.fromEntity(roomEntity)
    }

    @Transactional
    override fun deleteRoom(roomId: Long) {
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()
        roomRepository.delete(roomEntity)
    }

    @Transactional
    override fun searchRooms(
        name: String?,
        type: String?,
        minPrice: Double?,
        maxPrice: Double?,
        address: AddressSearchDTO?,
        maxOccupancy: Int?,
        rating: Double?,
        pageable: Pageable
    ): Page<Room> {
        return roomRepository.findAll({ root, query, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            name?.let {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%${it.lowercase()}%"))
            }

            type?.let {
                predicates.add(criteriaBuilder.equal(root.get<String>("type"), it))
            }

            minPrice?.let {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), it))
            }

            maxPrice?.let {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), it))
            }

            address?.let {
                it.country.let { country ->
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get<Address>("address").get("country")), country.lowercase()))
                }
                it.cityOrProvince?.let { cityOrProvince ->
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get<Address>("address").get("cityOrProvince")), cityOrProvince.lowercase()))
                }
                it.districtOrCounty?.let { districtOrCounty ->
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get<Address>("address").get("districtOrCounty")), districtOrCounty.lowercase()))
                }
                it.neighborhoodOrTown?.let { neighborhoodOrTown ->
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get<Address>("address").get("neighborhoodOrTown")), "%${neighborhoodOrTown.lowercase()}%"))
                }
            }

            maxOccupancy?.let {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("maxOccupancy"), it))
            }

            rating?.let {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), it))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }, pageable)
            .map { Room.fromEntity(it) }
    }
}
