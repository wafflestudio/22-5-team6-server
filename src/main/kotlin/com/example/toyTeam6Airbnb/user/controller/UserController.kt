package com.example.toyTeam6Airbnb.user.controller

import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
@RestController
@Tag(name = "User Controller", description = "User Controller API")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/api/v1/ping")
    @Operation(summary = "Ping pong", description = "Sample ping pong api for testing")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }

    @GetMapping("/api/v1/ping")
    fun getPing(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }

    @PostMapping("/api/auth/register")
    @Operation(summary = "회원가입", description = "유저 생성 및 프로필 이미지 업로드 URL 제공")
    fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<UrlResponse> {
        // Url 함게 반환하도록 수정
        val (user, url) = userService.register(request)
        return ResponseEntity.ok(UrlResponse(url))
    }

    // a mapping just for swagger testing
    // token parameter is passed as a query parameter
    // just return the token parameter in body
    @Operation(summary = "Redirect", description = "Redirect to the token", hidden = true)
    @GetMapping("/redirect")
    fun redirect(@RequestParam token: String, @RequestParam userid: Long): ResponseEntity<RedirectResponse> {
        return ResponseEntity.ok(RedirectResponse(token, userid))
    }

    @GetMapping("/users/liked-rooms")
    @Operation(summary = "사용자가 좋아요한 방 리스트(위시리스트) 얻기", description = "사용자가 좋아요를 누른 위시리스트를 받아옵니다. 페이지네이션 없이 통채로 가져옵니다.")
    fun getLikedRooms(
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ResponseEntity<List<Room>> {
        val userId = User.fromEntity(principalDetails.getUser()).id
        val likedRooms = userService.getLikedRooms(userId)
        return ResponseEntity.ok(likedRooms)
    }
}

data class RegisterRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val bio: String,
    val showMyReviews: Boolean,
    val showMyReservations: Boolean
)

data class RedirectResponse(
    val token: String,
    val userId: Long
)

data class UrlResponse(
    val imageUploadurl: String
)
