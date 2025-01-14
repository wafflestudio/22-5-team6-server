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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Review Controller", description = "Review Controller API - 리뷰는 예약 당 1개입니다")
class ReviewController(
    private val reviewService: ReviewService
) {

    @PostMapping
    @Operation(summary = "리뷰 생성", description = "새로운 리뷰를 생성합니다")
    fun createReview(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: CreateReviewRequest
    ): ResponseEntity<Review> {
        val review = reviewService.createReview(
            request.roomId,
            User.fromEntity(principalDetails.getUser()),
            request.reservationId,
            request.content,
            request.rating
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(review)
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "특정 방의 리뷰 조회", description = "특정 방의 모든 리뷰를 조회합니다")
    fun getReviewsByRoom(
        @PathVariable roomId: Long,
        @RequestParam pageable: Pageable
    ): ResponseEntity<Page<ReviewDTO>> {
        val reviews = reviewService.getReviewsByRoom(roomId, pageable)
        return ResponseEntity.ok(reviews)
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "특정 리뷰 상세 조회", description = "특정 리뷰의 상세정보를 조회합니다")
    fun getReviewDetails(
        @PathVariable reviewId: Long
    ): ResponseEntity<ReviewDTO> {
        val review = reviewService.getReviewDetails(reviewId)
        return ResponseEntity.ok(review)
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "특정 유저의 리뷰 조회", description = "특정 유저의 모든 리뷰를 조회합니다")
    fun getReviewsByUser(
        @PathVariable userId: Long,
        @RequestParam pageable: Pageable
    ): ResponseEntity<Page<ReviewDTO>> {
        val reviews = reviewService.getReviewsByUser(userId, pageable)
        return ResponseEntity.ok(reviews)
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "존재하는 리뷰를 수정합니다")
    fun updateReview(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reviewId: Long,
        @RequestBody request: UpdateReviewRequest
    ): ResponseEntity<Review> {
        val review = reviewService.updateReview(
            User.fromEntity(principalDetails.getUser()),
            reviewId,
            request.content,
            request.rating
        )
        return ResponseEntity.ok(review)
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다")
    fun deleteReview(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reviewId: Long
    ): ResponseEntity<Unit> {
        reviewService.deleteReview(User.fromEntity(principalDetails.getUser()), reviewId)
        return ResponseEntity.noContent().build()
    }
}

data class CreateReviewRequest(
    val roomId: Long,
    val reservationId: Long,
    val content: String,
    val rating: Int
)

data class UpdateReviewRequest(
    val content: String?,
    val rating: Int?
)
