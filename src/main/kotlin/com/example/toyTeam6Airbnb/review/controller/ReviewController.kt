package com.example.toyTeam6Airbnb.review.controller

import com.example.toyTeam6Airbnb.review.service.ReviewService
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.controller.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Review Controller", description = "Review Controller API - 리뷰는 예약 당 1개입니다")
class ReviewController(
    private val reviewService: ReviewService
) {

    @PostMapping
    @Operation(summary = "리뷰 생성", description = "새로운 리뷰를 생성합니다. 리뷰는 예약 당 1개입니다. Response body에는 생성된 review의 id가 들어갑니다.")
    fun createReview(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: CreateReviewRequest
    ): ResponseEntity<ReviewIdWithImage> {
        val reviewId = reviewService.createReview(
            User.fromEntity(principalDetails.getUser()),
            request.reservationId,
            request.content,
            request.rating
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewId)
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "특정 방의 리뷰 조회", description = "특정 방의 모든 리뷰를 조회합니다.")
    fun getReviewsByRoom(
        @PathVariable roomId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<ReviewByRoomDTO>> {
        val reviews = reviewService.getReviewsByRoom(roomId, pageable)
        return ResponseEntity.ok(reviews)
    }

    // hidden
    @GetMapping("/{reviewId}")
    @Operation(summary = "특정 리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다.", hidden = true)
    fun getReviewDetails(
        @PathVariable reviewId: Long
    ): ResponseEntity<ReviewDTO> {
        val review = reviewService.getReviewDetails(reviewId)
        return ResponseEntity.ok(review)
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "특정 유저의 리뷰 조회", description = "특정 유저의 모든 리뷰를 조회합니다.")
    fun getReviewsByUser(
        @PathVariable userId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<ReviewByUserDTO>> {
        val viewerId =
            try {
                val principalDetails = SecurityContextHolder.getContext().authentication.principal as PrincipalDetails
                principalDetails.getUser().id
                // logic for when the user is logged in
            } catch (e: ClassCastException) {
                // logic for when the user is not logged in
                null
            }
        val reviews = reviewService.getReviewsByUser(viewerId, userId, pageable)
        return ResponseEntity.ok(reviews)
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "존재하는 리뷰를 수정합니다. 수정할 내용은 content와 rating입니다. Response body에는 수정된 review의 id가 들어갑니다. POST API와의 통일성을 위해 response body에 review id가 포함되긴 하나, 수정 시에도 id는 바뀌지 않으므로 큰 의미는 없습니다.")
    fun updateReview(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reviewId: Long,
        @RequestBody request: UpdateReviewRequest
    ): ResponseEntity<ReviewIdWithImage> {
        val reviewIdWithImage = reviewService.updateReview(
            User.fromEntity(principalDetails.getUser()),
            reviewId,
            request.content,
            request.rating
        )
        return ResponseEntity.ok(reviewIdWithImage)
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    fun deleteReview(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reviewId: Long
    ): ResponseEntity<Unit> {
        reviewService.deleteReview(User.fromEntity(principalDetails.getUser()), reviewId)
        return ResponseEntity.noContent().build()
    }
}

data class CreateReviewRequest(
    val reservationId: Long,
    val content: String,
    val rating: Int
)

data class UpdateReviewRequest(
    val content: String?,
    val rating: Int?
)
