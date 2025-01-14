package com.example.toyTeam6Airbnb.profile

import com.example.toyTeam6Airbnb.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class ProfileException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null
) : DomainException(errorCode, httpStatusCode, msg, cause)

class ProfileAlreadyExistException : ProfileException(
    errorCode = 5001,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Profile already exists"
)

class ProfileNotFoundException : ProfileException(
    errorCode = 5002,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Profile not found"
)
