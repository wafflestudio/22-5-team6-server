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
    errorCode = 1007,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Room does not exist"
)

class RoomPermissionDeniedException : RoomException(
    errorCode = 1008,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Permission denied"
)
