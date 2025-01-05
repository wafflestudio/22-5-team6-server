package com.example.toyTeam6Airbnb.user

import com.example.toyTeam6Airbnb.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class UserException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null
) : DomainException(errorCode, httpStatusCode, msg, cause)

class SignUpUsernameConflictException : UserException(
    errorCode = 1001,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "Username conflict"
)

class SignUpBadUsernameException : UserException(
    errorCode = 1002,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Bad username"
)

class SignUpBadPasswordException : UserException(
    errorCode = 1003,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Bad password"
)

class SignInUserNotFoundException : UserException(
    errorCode = 1004,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "User not found"
)

class SignInInvalidPasswordException : UserException(
    errorCode = 1005,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "Invalid password"
)

class AuthenticateException : UserException(
    errorCode = 1006,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "Authenticate failed"
)

class OAuthException : UserException(
    errorCode = 1007,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "OAuth Exception"
)
