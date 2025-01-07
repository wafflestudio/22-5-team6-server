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
    errorCode = 1009,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Reservation is not Available"
)

class ReservationNotFound : ReservationException(
    errorCode = 1010,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "Reservation doesn't exist"
)

class ReservationPermissionDenied : ReservationException(
    errorCode = 1011,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Permission Denied"
)
