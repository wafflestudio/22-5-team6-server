package com.example.toyTeam6Airbnb.Image

import com.example.toyTeam6Airbnb.Image.persistence.ImageEntity
import com.example.toyTeam6Airbnb.Image.persistence.ImageRepository
import com.example.toyTeam6Airbnb.room.RoomNotFoundException
import com.example.toyTeam6Airbnb.room.persistence.RoomRepository
import com.example.toyTeam6Airbnb.user.UserNotFoundException
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
import java.util.concurrent.ConcurrentHashMap

@Service
class ImageService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val roomRepository: RoomRepository,
    @Autowired private val imageRepository: ImageRepository,
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

    // Presigned URL for Profile Image Upload
    // ImageEntity 생성해주고, 생성된 이미지 엔티티 Id를 가지고 filepath를 'Images/{이미지엔티티의 id}.jpg'로 설정
    // 이렇게 해서 이미지 엔티티의 id를 리턴해주면, 프론트에서 이미지 엔티티의 id를 받아서 이미지 엔티티의 id로 이미지를 가져올 수 있게 됨
    fun generateProfileImageUploadUrl(userId: Long): String {
        val userEntity = userRepository.findById(userId).orElseThrow { UserNotFoundException() }

        // userEntity의 Image가 null이 아니면 이미지가 존재하는 것이므로 해당 이미지 엔티티의 ID로 filePath 설정후 생성
        // if문으로 이미지가 존재하는지 확인하는 것이 좋을듯
        if (userEntity.image != null) {
            val imageId = userEntity.image!!.id
            val filePath = "Images/$imageId.jpg"
            filePathMap["images:$imageId"] = filePath
            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build()

            val presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .putObjectRequest(putObjectRequest)
                .build()

            return s3Presigner.presignPutObject(presignRequest).url().toString()
        } else {
            val imageEntity = ImageEntity(
                user = userEntity
            )
            val imageId = imageRepository.save(imageEntity).id
            val filePath = "Images/$imageId.jpg"
            filePathMap["images:$imageId"] = filePath
            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build()

            val presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .putObjectRequest(putObjectRequest)
                .build()

            return s3Presigner.presignPutObject(presignRequest).url().toString()
        }
    }

    // Generate download URL for profile image
    fun generateProfileImageDownloadUrl(userId: Long): String {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException() }
        val image = user.image ?: throw RuntimeException("No profile image found for user")
        val filePath = "Images/${image.id}.jpg"

        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60))
            .getObjectRequest(getObjectRequest)
            .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }

    // requestBody : roomId(long), Imageslot(long)
    // ImageSlot 개수만큼 이미지 엔티티 생성 매칭하는 roomEntity랑 매핑시킴
    // 이미지 엔티티의 id를 가지고 filepath를 'Rooms/{roomEntity의 id}/{이미지엔티티의 id}.jpg'로 설정
    // url 리스트를 반환해줌
    fun generateRoomImageUploadUrl(roomId: Long, imageSlot: Int): List<String> {
        val roomEntity = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException() }
        val imageUrls = mutableListOf<String>()

        // 해당 RoomID의 ImageEntity 개수가 몇개인지 체크
        // 1. ImageEntity 개수가 0이면, 이미지 슬롯 만큼 엔티티 생성 후 저장
        // 2. ImageEntity 개수가 slot보다 많으면, 이미 저장되어 있는 만큼 slot 개수만큼 url 생성후 반환 (abcde) (abc) // 저장공간 낭비
        // 3. ImageEntity 개수가 slot보다 적으면, 저장되어 있는 엔티티는 불러와서 url 생성후, 부족한 만큼 이미지 엔티티 생성 후 저장

        val existingImages = imageRepository.findByRoomId(roomId)
        if (existingImages.size >= imageSlot) {
            existingImages.take(imageSlot).forEach { imageEntity ->
                val filePath = "Images/${imageEntity.id}.jpg"
                filePathMap["images:${imageEntity.id}"] = filePath

                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build()

                val presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .putObjectRequest(putObjectRequest)
                    .build()

                imageUrls.add(s3Presigner.presignPutObject(presignRequest).url().toString())
            }
        } else {
            existingImages.forEach { imageEntity ->
                val filePath = "Images/${imageEntity.id}.jpg"
                filePathMap["images:${imageEntity.id}"] = filePath

                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build()

                val presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .putObjectRequest(putObjectRequest)
                    .build()

                imageUrls.add(s3Presigner.presignPutObject(presignRequest).url().toString())
            }
            // 새롭게 추가 되어야 하는 이미지 엔티티 생성
            for (i in existingImages.size until imageSlot) {
                val imageEntity = ImageEntity(
                    room = roomEntity
                )
                val imageId = imageRepository.save(imageEntity).id
                val filePath = "Images/${imageEntity.id}.jpg"
                filePathMap["images:${imageEntity.id}"] = filePath

                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build()

                val presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .putObjectRequest(putObjectRequest)
                    .build()

                imageUrls.add(s3Presigner.presignPutObject(presignRequest).url().toString())
            }
        }
        return imageUrls
    }

    // Presigned URL for Download Room Images
    // requestBody : roomId(long), Imageslot(long)
    // 방에대한 이미지를 다운로드할 수 있는 URL 생성
    // 룸 엔티티를 찾아서, 존재하는 이미지 개수만큼 이미지 엔티티의 id를 가지고 filepath를 'Images/{이미지엔티티의 id}.jpg'로 설정
    // 이미지가 아예 존재하지 않으면 Exception 발생 시키기
    fun generateRoomImageDownloadUrls(roomId: Long): List<String> {
        val roomEntity = roomRepository.findById(roomId).orElseThrow { RoomNotFoundException() }
        val imageEntities = imageRepository.findByRoomId(roomId)

        if (imageEntities.isEmpty()) {
            throw RuntimeException("No images for the Room")
        }

        // imageEntities 개수만큼 url 경로 만들어주도록 코드 수정
        return imageEntities.map { imageEntity ->
            val filePath = "Images/${imageEntity.id}.jpg"
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build()

            val presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build()

            s3Presigner.presignGetObject(presignRequest).url().toString()
        }
    }

    // Delete ImageEntity by ID
    // 이미지 엔티티의 id를 받아서 삭제
    // 룸자체 다 삭제
    // 룸 몇개만

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
