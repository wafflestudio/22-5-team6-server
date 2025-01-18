package com.example.toyTeam6Airbnb.image

import com.example.toyTeam6Airbnb.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class ImageException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null
) : DomainException(errorCode, httpStatusCode, msg, cause)

class ImageNotFoundException : ImageException(
    errorCode = 7001,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Image not found"
)