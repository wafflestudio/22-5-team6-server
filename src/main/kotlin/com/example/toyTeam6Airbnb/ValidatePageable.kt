package com.example.toyTeam6Airbnb.room

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

fun validatePageable(pageable: Pageable): Pageable {
    return PageRequest.of(pageable.pageNumber, pageable.pageSize)
}

// 이후에 rating, review, price 등의 정렬 기준이 추가될 경우 함수를 추가하여 처리할 수 있습니다.
