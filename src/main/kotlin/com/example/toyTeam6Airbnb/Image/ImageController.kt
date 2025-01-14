package com.example.toyTeam6Airbnb.Image

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageService
) {
    @Operation(
        summary = "Generate a presigned URL for uploading an image",
        description = "Generates a presigned URL for uploading an image directly to S3."
    )
    @GetMapping("/upload-url")
    fun generateUploadUrl(
        @RequestParam("key") key: String,
        @RequestParam("expirationMinutes", defaultValue = "10") expirationMinutes: Long
    ): ResponseEntity<String> {
        return try {
            val uploadUrl = imageService.generateUploadUrl(key, expirationMinutes)
            ResponseEntity.ok(uploadUrl)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to generate upload URL: ${e.message}")
        }
    }

    @Operation(
        summary = "Generate a presigned URL for downloading an image",
        description = "Generates a presigned URL for downloading an image directly from S3."
    )
    @GetMapping("/download-url")
    fun generateDownloadUrl(
        @RequestParam("key") key: String,
        @RequestParam("expirationMinutes", defaultValue = "60") expirationMinutes: Long
    ): ResponseEntity<String> {
        return try {
            val downloadUrl = imageService.generateDownloadUrl(key, expirationMinutes)
            ResponseEntity.ok(downloadUrl)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to generate download URL: ${e.message}")
        }
    }

//    // 내부 DTO 클래스 정의
//    data class UploadImageRequest(
//        @Schema(type = "string", format = "binary", description = "The image file to upload")
//        val file: MultipartFile,
//
//        @Schema(description = "The key to associate with the uploaded image", example = "example-key")
//        val key: String
//    )
}
