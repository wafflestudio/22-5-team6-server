package com.example.toyTeam6Airbnb.review

import com.example.toyTeam6Airbnb.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class ReviewException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null
) : DomainException(errorCode, httpStatusCode, msg, cause)

class ReviewNotFoundException : ReviewException(
    errorCode = 3001,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Review doesn't exist"
)

class ReviewPermissionDeniedException : ReviewException(
    errorCode = 3002,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Permission Denied"
)

class DuplicateReviewException : ReviewException(
    errorCode = 3003,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Review Already Exists"
)
