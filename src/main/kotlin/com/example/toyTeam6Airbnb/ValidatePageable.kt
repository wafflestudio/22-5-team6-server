package com.example.toyTeam6Airbnb

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

fun validateSort(pageable: Pageable, allowedSortProperties: List<String>) {
    if (pageable.sort.isSorted) {
        pageable.sort.forEach { order ->
            if (order.property !in allowedSortProperties) {
                throw WrongSortingException()
            }
        }
    }
}

fun validatePageableForRoom(pageable: Pageable): Pageable {
    val allowedSortProperties = listOf("createdAt", "id", "name", "price.perNight")
    validateSort(pageable, allowedSortProperties)
    return PageRequest.of(pageable.pageNumber, pageable.pageSize, pageable.sort)
}

fun validatePageableForReview(pageable: Pageable): Pageable {
    val allowedSortProperties = listOf("createdAt", "rating")
    validateSort(pageable, allowedSortProperties)
    return PageRequest.of(pageable.pageNumber, pageable.pageSize, pageable.sort)
}

fun validatePageableForReservation(pageable: Pageable): Pageable {
    val allowedSortProperties = listOf("createdAt", "startDate", "endDate")
    validateSort(pageable, allowedSortProperties)
    return PageRequest.of(pageable.pageNumber, pageable.pageSize, pageable.sort)
}

sealed class PageableException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null
) : DomainException(errorCode, httpStatusCode, msg, cause)

class WrongSortingException : PageableException(
    errorCode = 6001,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "Wrong form of sorting"
)
