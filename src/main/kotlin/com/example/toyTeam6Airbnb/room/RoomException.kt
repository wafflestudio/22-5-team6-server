package com.example.toyTeam6Airbnb.room

import com.example.toyTeam6Airbnb.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class RoomException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null
) : DomainException(errorCode, httpStatusCode, msg, cause)

class RoomNotFoundException : RoomException(
    errorCode = 2001,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Room does not exist"
)

class RoomPermissionDeniedException : RoomException(
    errorCode = 2002,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Permission denied"
)
