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

class InvalidMaxOccupancyException : RoomException(
    errorCode = 2003,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Invalid max occupancy"
)

class InvalidPriceException : RoomException(
    errorCode = 2004,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Invalid price"
)

class InvalidAddressException : RoomException(
    errorCode = 2005,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Invalid rating"
)

class InvalidNameException : RoomException(
    errorCode = 2006,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Invalid name"
)

class InvalidDescriptionException : RoomException(
    errorCode = 2007,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Invalid description"
)

class InvalidRoomTypeException : RoomException(
    errorCode = 2008,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Invalid room type"
)

class DuplicateRoomException : RoomException(
    errorCode = 2009,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Room already exists"
)

class RoomAlreadyLikedException : RoomException(
    errorCode = 2010,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Room already liked, 중복된 좋아요 요청이 넘어옴"
)

class RoomLikeNotFoundException : RoomException(
    errorCode = 2011,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Room like not found, 좋아요가 이미 없는데 취소요청이 넘어옴"
)

class InvalidRoomDetailsException : RoomException(
    errorCode = 2012,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Invalid room details"
)
