package com.example.toyTeam6Airbnb.Image

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Paths
import java.util.Date

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageService
) {

    // 이미지 업로드 엔드포인트
    @PostMapping("/upload")
    fun uploadImage(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("key") key: String
    ): ResponseEntity<String> {
        return try {
            val tempFile = Paths.get(System.getProperty("java.io.tmpdir"), file.originalFilename).toFile()
            file.transferTo(tempFile) // MultipartFile을 임시 파일로 저장

            imageService.uploadFile(tempFile.absolutePath, key) // S3에 업로드
            tempFile.delete() // 임시 파일 삭제

            ResponseEntity("Image uploaded successfully with key: $key", HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity("Failed to upload image: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/{key}")
    fun getImageSignedUrl(
        @PathVariable("key") key: String
    ): ResponseEntity<String> {
        return try {
            val expirationDate = Date(System.currentTimeMillis() + 3600_000) // 1시간 유효

            val signedUrl = imageService.generateSignedUrl(
                "https://d3m9s5wmwvsq01.cloudfront.net",
                key,
                expirationDate
            )

            ResponseEntity.ok(signedUrl)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to generate signed URL: ${e.message}")
        }
    }
}

//    // 이미지 다운로드 엔드포인트
//    @GetMapping("/download")
//    fun downloadImage(
//        @RequestParam("key") key: String,
//        @RequestParam("destination") destination: String
//    ): ResponseEntity<String> {
//        return try {
//            imageService.downloadFile(key, destination) // S3에서 다운로드
//            ResponseEntity("Image downloaded successfully to: $destination", HttpStatus.OK)
//        } catch (e: Exception) {
//            ResponseEntity("Failed to download image: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
//        }
//    }

//    // 이미지를 화면에 표시, 보안 취약
//    @GetMapping("/{key}")
//    fun getImage(
//        @PathVariable("key") key: String
//    ): ResponseEntity<Void> {
//        return try {
//            // CloudFront 배포 URL
//            val cloudFrontUrl = "https://d3m9s5wmwvsq01.cloudfront.net/$key"
//
//            // 302 Redirect
//            ResponseEntity.status(HttpStatus.FOUND)
//                .header("Location", cloudFrontUrl)
//                .build()
//        } catch (e: Exception) {
//            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(null)
//        }
//    }
