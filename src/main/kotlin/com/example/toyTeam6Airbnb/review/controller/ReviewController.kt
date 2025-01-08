package com.example.toyTeam6Airbnb.review.controller

import com.example.toyTeam6Airbnb.review.service.ReviewService
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.controller.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Review Controller", description = "Review Controller API")
class ReviewController(
    private val reviewService: ReviewService
) {

    @PostMapping
    @Operation(summary = "Create Review", description = "Create a new review")
    fun createReview(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: CreateReviewRequest
    ): ResponseEntity<ReviewDTO> {
        val review = reviewService.createReview(
            request.roomId,
            User.fromEntity(principalDetails.getUser()),
            request.reservationId,
            request.content,
            request.rating
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(review.toDTO())
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get Reviews", description = "Get all reviews for a room")
    fun getReviews(
        @PathVariable roomId: Long
    ): ResponseEntity<List<ReviewDTO>> {
        val reviews = reviewService.getReviews(roomId).map { it.toDTO() }
        return ResponseEntity.ok(reviews)
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get Review Details", description = "Get details of a specific review")
    fun getReviewDetails(
        @PathVariable reviewId: Long
    ): ResponseEntity<ReviewDTO> {
        val review = reviewService.getReviewDetails(reviewId)
        return ResponseEntity.ok(review.toDTO())
    }

    // Review에 수정 사항이 추가되면 파라미터 수정 필요
    @PutMapping("/{reviewId}")
    @Operation(summary = "Update Review", description = "Update an existing review")
    fun updateReview(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable reviewId: Long,
        @RequestBody request: UpdateReviewRequest
    ): ResponseEntity<ReviewDTO> {
        val review = reviewService.updateReview(
            User.fromEntity(principalDetails.getUser()),
            reviewId,
            request.content,
            request.rating
        )
        return ResponseEntity.ok(review.toDTO())
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete Review", description = "Delete a review")
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
