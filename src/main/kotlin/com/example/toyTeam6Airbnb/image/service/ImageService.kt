package com.example.toyTeam6Airbnb.image.service

import com.example.toyTeam6Airbnb.image.persistence.ImageEntity
import com.example.toyTeam6Airbnb.image.persistence.ImageRepository
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.UserNotFoundException
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Service
class ImageService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val roomRepository: RoomRepository,
    @Autowired private val imageRepository: ImageRepository
) {

    // @Value("\${cloudfront.private-key}")
    // private lateinit var privateKey: String

    // @Value("\${cloudfront.key-pair-id}")
    // private lateinit var keyPairId: String

    private val s3Client: S3Client = S3Client.builder()
        .region(Region.AP_NORTHEAST_2) // 원하는 리전 설정
        .build()

    private val s3Presigner: S3Presigner = S3Presigner.builder()
        .region(Region.AP_NORTHEAST_2)
        .build()

    private val bucketName: String = "waffle-team6-storage"
    private val cloudFrontUrl: String = "https://d3m9s5wmwvsq01.cloudfront.net"

    private fun generatePresignedUrl(filePath: String): String {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .cacheControl("no-cache, no-store, must-revalidate")
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60))
            .putObjectRequest(putObjectRequest)
            .build()

        return s3Presigner.presignPutObject(presignRequest).url().toString()
    }

    fun generateProfileImageUploadUrl(userId: Long): String {
        val userEntity = userRepository.findById(userId).orElseThrow { UserNotFoundException() }

        val imageId = userEntity.image?.id ?: run {
            val newImage = ImageEntity(user = userEntity)
            imageRepository.save(newImage).id
        }

        val filePath = "Images/$imageId"
        return generatePresignedUrl(filePath)
    }

    fun generateProfileImageDownloadUrl(userId: Long): String {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException() }
        val image = user.image ?: return ""

        return "$cloudFrontUrl/Images/${image.id}"
    }

    fun generateRoomImageUploadUrls(roomId: Long, imageSlot: Int): List<String> {
        val roomEntity = roomRepository.findById(roomId)
            .orElseThrow { RoomNotFoundException() }

        val existingImages = imageRepository.findByRoomId(roomId)

        return if (existingImages.size == imageSlot) {
            existingImages.map { generatePresignedUrl("Images/${it.id}") }
        } else if (existingImages.size > imageSlot) {
            val overSizedImages = existingImages.drop(imageSlot)
            overSizedImages.forEach { image ->
                imageRepository.delete(image)
                s3Client.deleteObject { it.bucket(bucketName).key("Images/${image.id}") }
            }

            existingImages.take(imageSlot).map {
                generatePresignedUrl("Images/${it.id}")
            }
        } else {
            val imageUrls = existingImages.map {
                generatePresignedUrl("Images/${it.id}")
            }.toMutableList()

            repeat(imageSlot - existingImages.size) {
                val imageEntity = imageRepository.save(ImageEntity(room = roomEntity))
                imageUrls += generatePresignedUrl("Images/${imageEntity.id}")
            }

            imageUrls
        }
    }

    fun generateRoomImageDownloadUrls(roomId: Long): List<String> {
        roomRepository.findById(roomId).orElseThrow { RoomNotFoundException() }
        val imageEntities = imageRepository.findByRoomId(roomId)

        if (imageEntities.isEmpty()) return emptyList()

        return imageEntities.map { imageEntity ->
            "$cloudFrontUrl/Images/${imageEntity.id}"
        }
    }

    fun generateRoomImageDownloadUrl(roomId: Long): String {
        roomRepository.findById(roomId).orElseThrow { RoomNotFoundException() }
        val imageEntities = imageRepository.findByRoomId(roomId)
        if (imageEntities.isEmpty()) return ""

        val imageEntity = imageEntities.first()
        return "$cloudFrontUrl/Images/${imageEntity.id}"
    }

    fun deleteRoomImages(roomId: Long) {
        roomRepository.findById(roomId).orElseThrow { RoomNotFoundException() }
        val imageEntities = imageRepository.findByRoomId(roomId)

        imageEntities.forEach { imageEntity ->
            s3Client.deleteObject { it.bucket(bucketName).key("Images/${imageEntity.id}") }
            imageRepository.delete(imageEntity)
        }
    }
}
