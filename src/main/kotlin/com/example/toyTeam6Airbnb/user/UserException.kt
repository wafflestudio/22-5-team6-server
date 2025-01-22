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

class SignInUnknownException : UserException(
    errorCode = 1004,
    httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
    msg = "Unknown sign-in error"
)

class SignInBadUsernameOrPasswordException : UserException(
    errorCode = 1005,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "Bad username or password"
)

class AuthenticateException : UserException(
    errorCode = 1006,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "Authenticate failed"
)

class OAuthException : UserException(
    errorCode = 1007,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Problem with OAuth provider"
)

class UserNotFoundException : UserException(
    errorCode = 1008,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "User not found"
)

// Not used in code, but added for documentation purposes
// Returned within ProfileExistenceFilter.kt
class UserWithNoProfileException : UserException(
    errorCode = 1009,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "User with no profile"
)

class JWTException : UserException(
    errorCode = 1010,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "JWT Token error"
)

class likedRoomsPermissionDenied : UserException(
    errorCode = 1011,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "Permission Denied"
)
