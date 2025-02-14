package com.example.toyTeam6Airbnb.profile.controller

import com.example.toyTeam6Airbnb.profile.service.ProfileService
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile Controller", description = "Profile Controller API")
class ProfileController(
    private val profileService: ProfileService
) {

    @GetMapping
    @Operation(summary = "유저 프로필 가져오기", description = "현재 로그인 되어 있는 user의 프로필을 가져옵니다. 이미지 조회 Url 제공")
    fun getCurrentUserProfile(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<Profile> {
        val profile = profileService.getCurrentUserProfile(principalDetails.getUser())
        return ResponseEntity.ok(profile)
    }

    @GetMapping("/{userId}")
    @Operation(summary = "특정 유저 프로필 가져오기", description = "특정 user의 프로필을 가져옵니다. 이미지 조회 Url 제공")
    fun getProfileByUserId(
        @PathVariable userId: Long
    ): ResponseEntity<Profile> {
        val profile = profileService.getProfileByUserId(userId)
        return ResponseEntity.ok(profile)
    }

    @PutMapping
    @Operation(summary = "유저 프로필 업데이트하기", description = "현재 로그인 되어 있는 user의 프로필을 업데이트합니다. 이미지 업로드 URL 제공")
    fun updateCurrentUserProfile(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<UrlResponse> {
        val urlResponse = profileService.updateCurrentUserProfile(principalDetails.getUser(), request)
        return ResponseEntity.ok(urlResponse)
    }

    @PostMapping
    @Operation(summary = "유저 프로필 추가", description = "현재 로그인 되어 있는 user에게 프로필을 추가합니다. (소셜 로그인 전용) 이미지 업로드 URL 제공")
    fun addProfileToCurrentUser(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: CreateProfileRequest
    ): ResponseEntity<UrlResponse> {
        val urlResponse = profileService.addProfileToCurrentUser(principalDetails.getUser(), request)
        return ResponseEntity.status(HttpStatus.CREATED).body(urlResponse)
    }
}

data class UpdateProfileRequest(
    val nickname: String,
    val bio: String,
    val showMyReviews: Boolean,
    val showMyReservations: Boolean,
    val showMyWishlist: Boolean
)

data class CreateProfileRequest(
    val nickname: String,
    val bio: String,
    val showMyReviews: Boolean,
    val showMyReservations: Boolean,
    val showMyWishlist: Boolean
)

data class UrlResponse(
    val imageUploadUrl: String
)
