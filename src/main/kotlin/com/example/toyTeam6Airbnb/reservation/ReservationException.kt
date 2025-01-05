package com.example.toyTeam6Airbnb.reservation

import com.example.toyTeam6Airbnb.DomainException
import com.example.toyTeam6Airbnb.reservation.controller.Reservation
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
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Reservation is not Available"
)

class RoomPermissionDeniedException : ReservationException(
    errorCode = 1010,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Permission denied"
)
