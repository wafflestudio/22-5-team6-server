package com.example.toyTeam6Airbnb

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

fun validatePageable(pageable: Pageable): Pageable {
    val allowedSortProperties = listOf("createdAt", "id", "name", "price")
    pageable.sort.forEach { order ->
        if (order.property !in allowedSortProperties) {
            throw WrongSortingException()
        }
    }

    return PageRequest.of(
        pageable.pageNumber,
        pageable.pageSize,
        pageable.sort
    )
}

fun validateSortedPageable(pageable: Pageable): Pageable {
    val allowedSortProperties = listOf("createdAt", "rating")
    pageable.sort.forEach { order ->
        if (order.property !in allowedSortProperties) {
            throw WrongSortingException()
        }
    }
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
// 이후에 rating, review, price 등의 정렬 기준이 추가될 경우 함수를 추가하여 처리할 수 있습니다.
