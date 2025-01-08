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

class ReviewNotFound : ReviewException(
    errorCode = 3001,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Review doesn't exist"
)

class ReviewPermissionDenied : ReviewException(
    errorCode = 3002,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Permission Denied"
)
