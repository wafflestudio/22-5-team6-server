package com.example.toyTeam6Airbnb.reservation

import com.example.toyTeam6Airbnb.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class ReservationException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null
) : DomainException(errorCode, httpStatusCode, msg, cause)

class ReservationUnavailable : ReservationException(
    errorCode = 4001,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Reservation is not Available"
)

class ReservationNotFound : ReservationException(
    errorCode = 4002,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Reservation doesn't exist"
)

class ReservationPermissionDenied : ReservationException(
    errorCode = 4003,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Permission Denied"
)

class MaxOccupancyExceeded : ReservationException(
    errorCode = 4004,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Max Occupancy Exceeded"
)
