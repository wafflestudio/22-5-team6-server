package com.example.toyTeam6Airbnb.Image

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner
import com.amazonaws.services.cloudfront.util.SignerUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.nio.file.Paths
import java.security.PrivateKey
import java.util.Date

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
    @Transactional
    fun uploadFile(filePath: String, key: String) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(),
            RequestBody.fromFile(Paths.get(filePath))
        )
    }

//    // 파일 다운로드 메서드
//    @Transactional
//    fun downloadFile(key: String, destination: String) {
//        s3Client.getObject(
//            GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build(),
//            Paths.get(destination)
//        )
//    }

    // CloudFront signed URL 생성 메서드
    fun generateSignedUrl(
        domain: String,
        key: String,
        expiration: Date
    ): String {
        // privateKey와 keyPairId는 GitHub Secrets에서 가져온 값이 자동으로 주입됨
        return createSignedUrl(domain, key, privateKey, keyPairId, expiration)
    }

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
