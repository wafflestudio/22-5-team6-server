package com.example.toyTeam6Airbnb.Image

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner
import com.amazonaws.services.cloudfront.util.SignerUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.security.PrivateKey
import java.util.Date
import java.util.UUID

@Service
class ImageService() {

    @Value("\${cloudfront.private-key}")
    private lateinit var privateKey: String

    @Value("\${cloudfront.key-pair-id}")
    private lateinit var keyPairId: String
    private val s3Client: S3Client = S3Client.builder()
        .region(Region.AP_NORTHEAST_2) // 원하는 리전 설정
        .build()
    private val bucketName: String = "waffle-team6-storage"
    private val cloudFrontUrl: String = "https://d3m9s5wmwvsq01.cloudfront.net"

    // 파일 업로드 메서드
    fun uploadFile(file: MultipartFile, key: String): String {
        val fileName = "$key/${UUID.randomUUID()}_${file.originalFilename}" // 고유 경로 생성
        try {
            val tempFile = Paths.get(System.getProperty("java.io.tmpdir"), file.originalFilename).toFile()
            file.transferTo(tempFile)

            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build(),
                RequestBody.fromFile(tempFile.toPath())
            )
            tempFile.delete()

            // DB에 파일 경로 저장 saveFilePathToDatabase(key, fileName) (추후 Room, User 엔티티에 추가식으로 수정)
            // ProfileImageUpload, RoomImageUplaod 형식으로 할것
        } catch (e: IOException) {
            throw RuntimeException("S3 업로드 중 에러가 발생했습니다: ${e.message}", e)
        }

        return "https://$bucketName.s3.amazonaws.com/$fileName"
    }

    // CloudFront signed URL 생성 메서드
    fun generateSignedUrl(key: String): String {
        try {
            if (privateKey.isBlank()) {
                throw IllegalStateException("Private key is not configured or is blank")
            }
            if (keyPairId.isBlank()) {
                throw IllegalStateException("Key Pair ID is not configured or is blank")
            }
            // S3 객체 경로 생성 (key 폴더 사용)
            val filePath = "$key/"

            // 만료 시간 설정 (예: 1시간 후)
            val expirationDate = Date(System.currentTimeMillis() + 3600_000)

            // CloudFront 서명된 URL 생성
            // 1. 임시 파일 생성 및 비공개 키 쓰기
            val tempFile = File.createTempFile("privateKey", ".pem")
            tempFile.deleteOnExit() // 애플리케이션 종료 시 자동 삭제
            tempFile.writeText(privateKey)

            // 2. Private Key 객체 로드
            val privateKeyObj: PrivateKey = SignerUtils.loadPrivateKey(tempFile)
            return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                "https://d3m9s5wmwvsq01.cloudfront.net/$filePath",
                keyPairId, // Key Pair ID
                privateKeyObj,
                expirationDate
            )
        } catch (e: Exception) {
            throw RuntimeException("Signed URL 생성 중 에러 발생: ${e.message}", e)
        }
    }
//    fun generateSignedUrl(
//        domain: String,
//        key: String,
//        expiration: Date
//    ): String {
//        // privateKey와 keyPairId는 GitHub Secrets에서 가져온 값이 자동으로 주입됨
//        if (privateKey.isBlank()) {
//            throw IllegalStateException("Private key is not configured or is blank")
//        }
//        if (keyPairId.isBlank()) {
//            throw IllegalStateException("Key Pair ID is not configured or is blank")
//        }
//        return createSignedUrl(domain, key, privateKey, keyPairId, expiration)
//    }

    // CloudFront 서명 URL 생성 메서드
    // createSignedUrl 메서드 구현
    private fun createSignedUrl(
        domain: String,
        key: String,
        privateKey: String,
        keyPairId: String,
        expiration: Date
    ): String {
        try {
            // 1. 임시 파일 생성 및 비공개 키 쓰기
            val tempFile = File.createTempFile("privateKey", ".pem")
            tempFile.deleteOnExit() // 애플리케이션 종료 시 자동 삭제
            tempFile.writeText(privateKey)

            // 2. Private Key 객체 로드
            val privateKeyObj: PrivateKey = SignerUtils.loadPrivateKey(tempFile)

            // 3. CloudFront 서명된 URL 생성
            return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                "$domain/$key", // 리소스 URL
                keyPairId, // Key Pair ID
                privateKeyObj, // PrivateKey 객체
                expiration // 만료 시간
            )
        } catch (e: Exception) {
            throw RuntimeException("Failed to create signed URL: ${e.message}", e)
        }
    }
}
