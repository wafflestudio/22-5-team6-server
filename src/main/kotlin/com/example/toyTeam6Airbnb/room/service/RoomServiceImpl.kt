package com.example.toyTeam6Airbnb.room.service

import com.example.toyTeam6Airbnb.Image.ImageService
import com.example.toyTeam6Airbnb.review.persistence.ReviewRepository
import com.example.toyTeam6Airbnb.room.DuplicateRoomException
import com.example.toyTeam6Airbnb.room.InvalidAddressException
import com.example.toyTeam6Airbnb.room.InvalidDescriptionException
import com.example.toyTeam6Airbnb.room.InvalidMaxOccupancyException
import com.example.toyTeam6Airbnb.room.InvalidNameException
import com.example.toyTeam6Airbnb.room.InvalidPriceException
import com.example.toyTeam6Airbnb.room.InvalidRoomTypeException
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.RoomPermissionDeniedException
import com.example.toyTeam6Airbnb.room.controller.AddressSearchDTO
import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.controller.RoomDetailsDTO
import com.example.toyTeam6Airbnb.room.controller.RoomShortDTO
import com.example.toyTeam6Airbnb.room.persistence.Address
import com.example.toyTeam6Airbnb.room.persistence.Price
import com.example.toyTeam6Airbnb.room.persistence.RoomDetails
import com.example.toyTeam6Airbnb.room.persistence.RoomEntity
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.room.persistence.RoomType
import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import com.example.toyTeam6Airbnb.validatePageable
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class RoomServiceImpl(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val imageService: ImageService
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
        val hostEntity = userRepository.findByIdOrNull(hostId) ?: throw AuthenticateException()

        validateRoomInfo(name, description, type, address, price, maxOccupancy)

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

            val imageUploadUrl = imageService.generateRoomImageUploadUrl(roomEntity.id!!, imageSlot) // 리스트로 이미지 업로드 URL 이미지 개수만큼 생성

            return RoomShortDTO.fromEntity(roomEntity, imageUploadUrl)
        } catch (e: DataIntegrityViolationException) {
            throw DuplicateRoomException()
        }
    }

    @Transactional
    override fun getRooms(pageable: Pageable): Page<Room> {
        // Room에서 이미지 url 가져오기
        // imageService의 generateRoomImageDownloadUrls() 사용후 첫번쨰 String만 가져오기
        return roomRepository.findAll(validatePageable(pageable)).map {
            val imageUrl = imageService.generateRoomImageDownloadUrls(it.id!!).first() // Null 처리는 imageService에서 처리
            Room.fromEntity(it, imageUrl)
        }
    }

    @Transactional
    override fun getRoomDetails(roomId: Long): RoomDetailsDTO {
        val roomEntity = roomRepository.findByIdOrNull(roomId) ?: throw RoomNotFoundException()
        // roomEntity에 종속된 이미지 리스트들 얻어오기
        val imageUrlList = imageService.generateRoomImageDownloadUrls(roomEntity.id!!) // 이미지 리스트 가져오기
        return RoomDetailsDTO.fromEntity(roomEntity, imageUrlList)
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
        imageSlot: Int // imageSlot Request Body에 추가
    ): RoomShortDTO {
        val hostEntity = userRepository.findByIdOrNull(hostId) ?: throw AuthenticateException()
        val roomEntity = roomRepository.findByIdOrNullForUpdate(roomId) ?: throw RoomNotFoundException()

        if (roomEntity.host.id != hostEntity.id) throw RoomPermissionDeniedException()

        validateRoomInfo(name, description, type, address, price, maxOccupancy)

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

        val imageUploadUrl = imageService.generateRoomImageUploadUrl(roomEntity.id!!, imageSlot) // 리스트로 이미지 업로드 URL 이미지 개수만큼 생성
        return RoomShortDTO.fromEntity(roomEntity, imageUploadUrl)
    }

    @Transactional
    override fun deleteRoom(
        userId: Long,
        roomId: Long
    ) {
        val hostEntity = userRepository.findByIdOrNull(userId) ?: throw AuthenticateException()
        val roomEntity = roomRepository.findByIdOrNullForUpdate(roomId) ?: throw RoomNotFoundException()

        if (roomEntity.host.id != hostEntity.id) throw RoomPermissionDeniedException()

        // 이미지 엔티티도 삭제하도록 하기
        // imageEntity delete
        roomRepository.delete(roomEntity)
    }

    @Transactional
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
        pageable: Pageable
    ): Page<Room> {
        val rooms = roomRepository.searchAvailableRooms(
            name = name,
            type = type,
            minPrice = minPrice,
            maxPrice = maxPrice,
            maxOccupancy = maxOccupancy,
            rating = rating,
            startDate = startDate,
            endDate = endDate,
            sido = address?.sido,
            sigungu = address?.sigungu,
            street = address?.street,
            detail = address?.detail,
            pageable = validatePageable(pageable)
        )
        return rooms.map {
            val imageUrl = imageService.generateRoomImageDownloadUrls(it.id!!).first() // Null 처리는 imageService에서 처리
            Room.fromEntity(it, imageUrl)
        }
    }

    private fun validateRoomInfo(
        name: String,
        description: String,
        type: RoomType,
        address: Address,
        price: Price,
        maxOccupancy: Int
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
}
