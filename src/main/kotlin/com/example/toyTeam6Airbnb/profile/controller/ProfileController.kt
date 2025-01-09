package com.example.toyTeam6Airbnb.profile.controller

import com.example.toyTeam6Airbnb.profile.service.ProfileService
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
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
    @Operation(summary = "Get User Profile", description = "Get the profile of the current user")
    fun getCurrentUserProfile(@AuthenticationPrincipal principalDetails: PrincipalDetails): ResponseEntity<Profile> {
        val profile = profileService.getCurrentUserProfile(principalDetails.getUser())
        return if (profile != null) {
            ResponseEntity.ok(Profile.fromEntity(profile))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping
    @Operation(summary = "Update User Profile", description = "Update the profile of the current user")
    fun updateCurrentUserProfile(@AuthenticationPrincipal principalDetails: PrincipalDetails, @RequestBody request: UpdateProfileRequest): ResponseEntity<Profile> {
        val updatedProfile = profileService.updateCurrentUserProfile(principalDetails.getUser(), request)
        return ResponseEntity.ok(Profile.fromEntity(updatedProfile))
    }

    @PostMapping
    @Operation(summary = "Add Profile to User", description = "Add a profile to the current user, only for users logged in with social login")
    fun addProfileToCurrentUser(@AuthenticationPrincipal principalDetails: PrincipalDetails, @RequestBody request: CreateProfileRequest): ResponseEntity<Profile> {
        val newProfile = profileService.addProfileToCurrentUser(principalDetails.getUser(), request)
        return ResponseEntity.status(201).body(Profile.fromEntity(newProfile))
    }
}

data class UpdateProfileRequest(
    val nickname: String
)

data class CreateProfileRequest(
    val nickname: String
)
