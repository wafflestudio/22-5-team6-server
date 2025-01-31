package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.image.service.ImageService
import com.example.toyTeam6Airbnb.reservation.persistence.ReservationRepository
import com.example.toyTeam6Airbnb.room.DuplicateRoomException
import com.example.toyTeam6Airbnb.room.InvalidAddressException
import com.example.toyTeam6Airbnb.room.InvalidDescriptionException
import com.example.toyTeam6Airbnb.room.InvalidMaxOccupancyException
import com.example.toyTeam6Airbnb.room.InvalidNameException
import com.example.toyTeam6Airbnb.room.InvalidPriceException
import com.example.toyTeam6Airbnb.room.InvalidRoomDetailsException
import com.example.toyTeam6Airbnb.room.InvalidRoomTypeException
import com.example.toyTeam6Airbnb.room.RoomAlreadyLikedException
import com.example.toyTeam6Airbnb.room.RoomLikeNotFoundException
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.RoomPermissionDeniedException
import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.controller.RoomByUserDTO
import com.example.toyTeam6Airbnb.room.controller.RoomDetailSearchDTO
import com.example.toyTeam6Airbnb.room.controller.RoomDetailsDTO
import com.example.toyTeam6Airbnb.room.controller.RoomShortDTO
import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomLikeEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomLikeRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomSpecifications
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import com.example.toyTeam6Airbnb.user.UserNotFoundException
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import com.example.toyTeam6Airbnb.validatePageableForRoom
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class RoomServiceImpl(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val imageService: ImageService,
    private val reservationRepository: ReservationRepository,
    private val roomLikeRepository: RoomLikeRepository
) : RoomService {

    @Transactional
    override fun createRoom(
        hostId: Long,
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        roomDetails: RoomDetails,
        price: Price,
        maxOccupancy: Int,
        imageSlot: Int // imageSlot Request Body에 추가
    ): RoomShortDTO {
        val hostEntity = userRepository.findByIdOrNull(hostId) ?: throw UserNotFoundException()

        validateRoomInfo(name, description, type, address, price, maxOccupancy, roomDetails)

        if (roomRepository.existsByAddress(address)) throw DuplicateRoomException()

        try {
            val roomEntity = RoomEntity(
                host = hostEntity,
                name = name,
                description = description,
                type = type,
                address = address,
                roomDetails = roomDetails,
                price = price,
                maxOccupancy = maxOccupancy,
                reservations = emptyList(),
                reviews = emptyList()
            ).let {
                roomRepository.save(it)
            }

            val imageUploadUrls = imageService.generateRoomImageUploadUrls(roomEntity.id!!, imageSlot) // 리스트로 이미지 업로드 URL 이미지 개수만큼 생성

            return RoomShortDTO.fromEntity(roomEntity, imageUploadUrls)
        } catch (e: DataIntegrityViolationException) {
            throw DuplicateRoomException()
        }
    }

    @Transactional
    override fun getRooms(viewerId: Long?, pageable: Pageable): Page<Room> {
        val roomEntities = roomRepository.findAll(validatePageableForRoom(pageable))
        val likedRoomIds = getLikedRoomIds(viewerId, roomEntities)

        return roomEntities.map { roomEntity ->
            val roomId = roomEntity.id!!
            val isLiked = likedRoomIds.contains(roomId)
            val imageUrl = imageService.generateRoomImageDownloadUrl(roomId)
            Room.fromEntity(roomEntity, imageUrl, isLiked)
        }
    }

    @Transactional
    override fun getRoomDetails(viewerId: Long?, roomId: Long): RoomDetailsDTO {
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()
        val imageUrlList = imageService.generateRoomImageDownloadUrls(roomEntity.id!!)
        val isLiked = viewerId?.let { roomLikeRepository.existsByUserIdAndRoomId(it, roomId) } ?: false
        return RoomDetailsDTO.fromEntity(roomEntity, imageUrlList, isLiked)
    }

    @Transactional
    override fun getRoomsByHostId(hostId: Long, pageable: Pageable): Page<RoomByUserDTO> {
        userRepository.findByIdOrNull(hostId) ?: throw UserNotFoundException()

        val roomsByHost = roomRepository.findAllByHostId(hostId, validatePageableForRoom(pageable))
        return roomsByHost.map { room ->
            val imageUrl = imageService.generateRoomImageDownloadUrl(room.id!!)
            RoomByUserDTO.fromEntity(room, imageUrl)
        }
    }

    @Transactional
    override fun updateRoom(
        hostId: Long,
        roomId: Long,
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        roomDetails: RoomDetails,
        price: Price,
        maxOccupancy: Int,
        imageSlot: Int
    ): RoomShortDTO {
        val hostEntity = userRepository.findByIdOrNull(hostId) ?: throw UserNotFoundException()
        val roomEntity = roomRepository.findByIdOrNullForUpdate(roomId) ?: throw RoomNotFoundException()

        if (roomEntity.host.id != hostEntity.id) throw RoomPermissionDeniedException()

        validateRoomInfo(name, description, type, address, price, maxOccupancy, roomDetails)

        if (roomRepository.existsByAddress(address) &&
            (roomEntity.address != address)
        ) {
            throw DuplicateRoomException()
        }

        roomEntity.name = name
        roomEntity.description = description
        roomEntity.type = type
        roomEntity.address = address
        roomEntity.price = price
        roomEntity.maxOccupancy = maxOccupancy

        roomRepository.save(roomEntity)

        val imageUploadUrls = imageService.generateRoomImageUploadUrls(roomEntity.id!!, imageSlot)
        return RoomShortDTO.fromEntity(roomEntity, imageUploadUrls)
    }

    @Transactional
    override fun deleteRoom(
        userId: Long,
        roomId: Long
    ) {
        val hostEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val roomEntity = roomRepository.findByIdOrNullForUpdate(roomId) ?: throw RoomNotFoundException()

        if (roomEntity.host.id != hostEntity.id) throw RoomPermissionDeniedException()

        imageService.deleteRoomImages(roomId)
        roomRepository.delete(roomEntity)
    }

    @Transactional(readOnly = true)
    override fun searchRooms(
        name: String?,
        type: RoomType?,
        minPrice: Double?,
        maxPrice: Double?,
        address: AddressSearchDTO?,
        maxOccupancy: Int?,
        rating: Double?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        roomDetails: RoomDetailSearchDTO?,
        viewerId: Long?,
        pageable: Pageable
    ): Page<Room> {
        val (valStartDate, valEndDate) = validateDates(startDate, endDate)

        val spec = Specification.where(RoomSpecifications.hasName(name))
            .and(RoomSpecifications.hasType(type))
            .and(RoomSpecifications.hasPriceBetween(minPrice, maxPrice))
            .and(RoomSpecifications.hasMaxOccupancy(maxOccupancy))
            .and(RoomSpecifications.isAvailable(valStartDate, valEndDate))
            .and(RoomSpecifications.hasAddress(address))
            .and(RoomSpecifications.hasRoomDetails(roomDetails))
            .and(RoomSpecifications.hasRating(rating))

        val roomEntities = roomRepository.findAll(spec, validatePageableForRoom(pageable))
        val likedRoomIds = getLikedRoomIds(viewerId, roomEntities)

        return roomEntities.map { roomEntity ->
            val roomId = roomEntity.id!!
            val isLiked = likedRoomIds.contains(roomId)
            val imageUrl = imageService.generateRoomImageDownloadUrl(roomId)
            Room.fromEntity(roomEntity, imageUrl, isLiked)
        }
    }

    fun validateDates(startDate: LocalDate?, endDate: LocalDate?): Pair<LocalDate?, LocalDate?> {
        return when {
            startDate != null && endDate == null -> {
                Pair(startDate, startDate.plusDays(1))
            }
            endDate != null && startDate == null -> {
                Pair(endDate.minusDays(1), endDate)
            }
            else -> {
                Pair(startDate, endDate)
            }
        }
    }

    @Transactional
    override fun toggleLike(
        userId: Long,
        roomId: Long
    ): Boolean {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val roomEntity = roomRepository.findByIdOrNullForUpdate(roomId) ?: throw RoomNotFoundException()

        val existingLike = roomLikeRepository.findByUserIdAndRoomId(userId, roomId)

        return if (existingLike != null) {
            roomLikeRepository.delete(existingLike)
            false
        } else {
            val roomLikeEntity = RoomLikeEntity(user = userEntity, room = roomEntity)
            try {
                roomLikeRepository.save(roomLikeEntity)
            } catch (e: DataIntegrityViolationException) {
                return true
            }
            true
        }
    }

    @Transactional
    override fun getHotPlacesByDate(viewerId: Long?, startDate: LocalDate, endDate: LocalDate): Page<Room> {
        val reservations = reservationRepository.findReservationsByDateRange(startDate, endDate)

        if (reservations.isEmpty()) return Page.empty()

        val sigunguReservationCounts = reservations.groupingBy { it.room.address.sigungu }
            .eachCount()

        val mostReservedSigungu = sigunguReservationCounts.maxByOrNull { it.value }!!.key

        val roomEntities = roomRepository.findTopRoomsBySigungu(mostReservedSigungu, Pageable.ofSize(3))
        val likedRoomIds = getLikedRoomIds(viewerId, roomEntities)

        return roomEntities.map { roomEntity ->
            val roomId = roomEntity.id!!
            val isLiked = likedRoomIds.contains(roomId)
            val imageUrl = imageService.generateRoomImageDownloadUrl(roomId)
            Room.fromEntity(roomEntity, imageUrl, isLiked)
        }
    }

    override fun getViewerId(): Long? {
        val viewerId =
            try {
                val principalDetails = SecurityContextHolder.getContext().authentication.principal as PrincipalDetails
                principalDetails.getUser().id
                // logic for when the user is logged in
            } catch (e: ClassCastException) {
                // logic for when the user is not logged in
                null
            }
        return viewerId
    }

    private fun validateRoomInfo(
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        price: Price,
        maxOccupancy: Int,
        roomDetails: RoomDetails
    ) {
        if (name.isBlank()) throw InvalidNameException()
        if (description.isBlank()) throw InvalidDescriptionException()
        validatePrice(price)
        if (maxOccupancy <= 0) throw InvalidMaxOccupancyException()

        try {
            RoomType.valueOf(type.name)
        } catch (e: IllegalArgumentException) {
            throw InvalidRoomTypeException()
        }

        validateAddress(address)
        validateRoomDetails(roomDetails)
    }

    private fun validatePrice(price: Price) {
        if (price.perNight <= 0 ||
            price.cleaningFee < 0 ||
            price.charge < 0 ||
            price.updateTotal() <= 0
        ) {
            throw InvalidPriceException()
        }
    }

    private fun validateAddress(address: Address) {
        if (address.sido.isBlank() ||
            address.sigungu.isBlank() ||
            address.street.isBlank() ||
            address.detail.isBlank()
        ) {
            throw InvalidAddressException()
        }
    }

    private fun validateRoomDetails(roomDetails: RoomDetails) {
        if (roomDetails.bedroom <= 0 ||
            roomDetails.bathroom <= 0 ||
            roomDetails.bed <= 0
        ) {
            throw InvalidRoomDetailsException()
        }
    }

    private fun getLikedRoomIds(viewerId: Long?, roomEntities: Page<RoomEntity>): Set<Long> {
        val roomIds = roomEntities.content.mapNotNull { it.id }

        return if (viewerId != null && roomIds.isNotEmpty()) {
            roomLikeRepository.findByUserIdAndRoomIdIn(viewerId, roomIds)
                .mapNotNull { it.room.id }
                .toSet()
        } else {
            emptySet()
        }
    }
}
