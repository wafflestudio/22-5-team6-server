package com.example.toyTeam6Airbnb.Image

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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
    // 업로드 URL 생성
    @PostMapping("/upload")
    fun generateUploadUrl(@RequestBody request: ImageRequest): ResponseEntity<Map<String, String>> {
        val uploadUrl = imageService.generateUploadUrl(request.resourceType, request.resourceId)
        return ResponseEntity.ok(mapOf("uploadUrl" to uploadUrl))
    }

    @Operation(
        summary = "Generate a presigned URL for downloading an image",
        description = "Generates a presigned URL for downloading an image directly from S3."
    )
    // 다운로드 URL 생성
    @PostMapping("/download")
    fun generateDownloadUrl(@RequestBody request: ImageRequest): ResponseEntity<Map<String, String>> {
        val downloadUrl = imageService.generateDownloadUrl(request.resourceType, request.resourceId)
        return ResponseEntity.ok(mapOf("downloadUrl" to downloadUrl))
    }
}

data class ImageRequest(
    val resourceType: String,
    val resourceId: String
)
