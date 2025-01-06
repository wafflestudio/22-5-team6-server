package com.example.toyTeam6Airbnb.room

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

fun validatePageable(pageable: Pageable, validSortFields: List<String>): Pageable {
    val orders = pageable.sort.mapNotNull { order ->
        if (validSortFields.contains(order.property)) {
            order
        } else {
            null
        }
    }.toList()

    return PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(orders))
}
