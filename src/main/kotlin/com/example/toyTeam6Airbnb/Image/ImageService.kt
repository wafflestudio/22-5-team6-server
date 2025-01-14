package com.example.toyTeam6Airbnb.Image

import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springdoc.webmvc.ui.SwaggerResourceResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class ImageService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val roomRepository: RoomRepository,
    private val swaggerResourceResolver: SwaggerResourceResolver
) {

    @Value("\${cloudfront.private-key}")
    private lateinit var privateKey: String

    @Value("\${cloudfront.key-pair-id}")
    private lateinit var keyPairId: String

    private val s3Client: S3Client = S3Client.builder()
        .region(Region.AP_NORTHEAST_2) // 원하는 리전 설정
        .build()

    private val s3Presigner: S3Presigner = S3Presigner.builder()
        .region(Region.AP_NORTHEAST_2)
        .build()

    private val bucketName: String = "waffle-team6-storage"
    private val cloudFrontUrl: String = "https://d3m9s5wmwvsq01.cloudfront.net"
    private val filePathMap: MutableMap<String, String> = ConcurrentHashMap() // 리소스 타입과 ID로 파일 경로 매핑

    // Presigned URL for Upload
    fun generateUploadUrl(resourceType: String, resourceId: String): String {
        val filePath = "$resourceType/$resourceId/${UUID.randomUUID()}_image.jpg"
        filePathMap["$resourceType:$resourceId"] = filePath // 리소스별 경로 매핑

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60))
            .putObjectRequest(putObjectRequest)
            .build()

        val uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString()

        if (resourceType == "users") {
            val userId = resourceId.toLongOrNull() ?: throw RuntimeException("Invalid user ID")
            val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
            user.imageUploadUrl = uploadUrl
            userRepository.save(user)
        } else if (resourceType == "rooms") {
            val roomId = resourceId.toLongOrNull() ?: throw RuntimeException("Invalid room ID")
            val room = roomRepository.findById(roomId).orElseThrow { RuntimeException("Room not found") }
            room.imageDownloadUrl = uploadUrl
            roomRepository.save(room)
        }

        return uploadUrl
    }

    // Presigned URL for Download
    fun generateDownloadUrl(resourceType: String, resourceId: String): String {
        val filePath = filePathMap["$resourceType:$resourceId"]
            ?: throw IllegalArgumentException("No file found for the given resource: $resourceType with ID: $resourceId")

        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60))
            .getObjectRequest(getObjectRequest)
            .build()

        val downloadUrl = s3Presigner.presignGetObject(presignRequest).url().toString()

        if (resourceType == "users") {
            val userId = resourceId.toLongOrNull() ?: throw RuntimeException("Invalid user ID")
            val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
            user.imageDownloadUrl = downloadUrl
            userRepository.save(user)
        } else if (resourceType == "rooms") {
            val roomId = resourceId.toLongOrNull() ?: throw RuntimeException("Invalid room ID")
            val room = roomRepository.findById(roomId).orElseThrow { RuntimeException("Room not found") }
            room.imageDownloadUrl = downloadUrl
            roomRepository.save(room)
        }

        return downloadUrl
    }

//    // 파일 업로드 메서드
//    fun uploadFile(file: MultipartFile, key: String): String {
//        val fileName = "$key/${UUID.randomUUID()}_${file.originalFilename}" // 고유 경로 생성
//        try {
//            val tempFile = Paths.get(System.getProperty("java.io.tmpdir"), file.originalFilename).toFile()
//            file.transferTo(tempFile)
//
//            s3Client.putObject(
//                PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileName)
//                    .build(),
//                RequestBody.fromFile(tempFile.toPath())
//            )
//            tempFile.delete()
//
//            // DB에 파일 경로 저장 saveFilePathToDatabase(key, fileName) (추후 Room, User 엔티티에 추가식으로 수정)
//            // ProfileImageUpload, RoomImageUplaod 형식으로 할것
//        } catch (e: IOException) {
//            throw RuntimeException("S3 업로드 중 에러가 발생했습니다: ${e.message}", e)
//        }
//
//        return "https://$bucketName.s3.amazonaws.com/$fileName"
//    }
//
//    // CloudFront signed URL 생성 메서드
//    fun generateSignedUrl(key: String): String {
//        try {
//            if (privateKey.isBlank()) {
//                throw IllegalStateException("Private key is not configured or is blank")
//            }
//            if (keyPairId.isBlank()) {
//                throw IllegalStateException("Key Pair ID is not configured or is blank")
//            }
//            // S3 객체 경로 생성 (key 폴더 사용)
//            val filePath = "$key/"
//
//            // 만료 시간 설정 (예: 1시간 후)
//            val expirationDate = Date(System.currentTimeMillis() + 3600_000)
//
//            // CloudFront 서명된 URL 생성
//            // 1. 임시 파일 생성 및 비공개 키 쓰기
//            val tempFile = File.createTempFile("privateKey", ".pem")
//            tempFile.deleteOnExit() // 애플리케이션 종료 시 자동 삭제
//            tempFile.writeText(privateKey)
//
//            // 2. Private Key 객체 로드
//            val privateKeyObj: PrivateKey = SignerUtils.loadPrivateKey(tempFile)
//            return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
//                "https://d3m9s5wmwvsq01.cloudfront.net/$filePath",
//                keyPairId, // Key Pair ID
//                privateKeyObj,
//                expirationDate
//            )
//        } catch (e: Exception) {
//            throw RuntimeException("Signed URL 생성 중 에러 발생: ${e.message}", e)
//        }
//    }
//
//    // CloudFront 서명 URL 생성 메서드
//    // createSignedUrl 메서드 구현
//    private fun createSignedUrl(
//        domain: String,
//        key: String,
//        privateKey: String,
//        keyPairId: String,
//        expiration: Date
//    ): String {
//        try {
//            // 1. 임시 파일 생성 및 비공개 키 쓰기
//            val tempFile = File.createTempFile("privateKey", ".pem")
//            tempFile.deleteOnExit() // 애플리케이션 종료 시 자동 삭제
//            tempFile.writeText(privateKey)
//
//            // 2. Private Key 객체 로드
//            val privateKeyObj: PrivateKey = SignerUtils.loadPrivateKey(tempFile)
//
//            // 3. CloudFront 서명된 URL 생성
//            return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
//                "$domain/$key", // 리소스 URL
//                keyPairId, // Key Pair ID
//                privateKeyObj, // PrivateKey 객체
//                expiration // 만료 시간
//            )
//        } catch (e: Exception) {
//            throw RuntimeException("Failed to create signed URL: ${e.message}", e)
//        }
//    }
}
