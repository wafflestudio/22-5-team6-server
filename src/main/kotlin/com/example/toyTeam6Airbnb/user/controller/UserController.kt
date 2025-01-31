package com.example.toyTeam6Airbnb.user.controller

import com.example.toyTeam6Airbnb.room.controller.Room
import com.example.toyTeam6Airbnb.room.service.RoomService
import com.example.toyTeam6Airbnb.user.JwtTokenProvider
import com.example.toyTeam6Airbnb.user.TokenDto
import com.example.toyTeam6Airbnb.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
@RestController
@Tag(name = "User Controller", description = "User Controller API")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val roomService: RoomService
) {
    @PostMapping("/api/auth/register")
    @Operation(summary = "회원가입", description = "유저 생성 및 프로필 이미지 업로드 URL 제공")
    fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<UrlResponse> {
        val (user, url) = userService.register(request)
        return ResponseEntity.ok(UrlResponse(url))
    }

    @PostMapping("/api/auth/login", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    @Operation(summary = "로그인", description = "유저 로그인")
    fun fakeLogin(
        @RequestBody request: LoginRequest
    ): ResponseEntity<String> {
        return ResponseEntity.ok("Fake endpoint to bypass Swagger login endpoint bug. This method shouldn't be called. It must be shadowed by Spring Security login endpoint.")
    }

    @PostMapping("/api/auth/reissueToken")
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 이용하여 엑세스 토큰 재발급")
    fun refreshToken(
        @RequestParam refreshToken: String
    ): ResponseEntity<TokenDto> {
        return ResponseEntity.ok(jwtTokenProvider.reissueToken(refreshToken))
    }

    // a mapping just for swagger testing
    // token parameter is passed as a query parameter
    // just return the token parameter in body
    @Operation(summary = "Redirect", description = "Redirect to the token", hidden = true)
    @GetMapping("/redirect")
    fun redirect(
        @RequestParam token: String,
        @RequestParam refreshToken: String,
        @RequestParam userid: Long
    ): ResponseEntity<RedirectResponse> {
        return ResponseEntity.ok(RedirectResponse(token, refreshToken, userid))
    }

    @GetMapping("/api/v1/users/{userId}/liked-rooms")
    @Operation(summary = "사용자가 좋아요한 방 리스트(위시리스트) 얻기", description = "특정 사용자가 좋아요를 누른 위시리스트를 받아옵니다. 페이지네이션 O")
    fun getLikedRooms(
        @PathVariable userId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<Room>> {
        val viewerId = roomService.getViewerId()
        val likedRooms = userService.getLikedRooms(viewerId, userId, pageable)
        return ResponseEntity.ok(likedRooms)
    }
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val bio: String,
    val showMyReviews: Boolean,
    val showMyReservations: Boolean,
    val showMyWishlist: Boolean
)

data class RedirectResponse(
    val token: String,
    val refreshToken: String,
    val userId: Long
)

data class UrlResponse(
    val imageUploadUrl: String
)
