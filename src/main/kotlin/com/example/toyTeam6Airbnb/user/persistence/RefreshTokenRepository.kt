package com.example.toyTeam6Airbnb.user.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByToken(token: String): RefreshTokenEntity?
    fun deleteByToken(token: String)
    fun deleteByUser(user: UserEntity)
}
