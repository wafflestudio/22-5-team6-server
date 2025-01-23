package com.example.toyTeam6Airbnb.user.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?

    fun existsByUsername(username: String): Boolean

    fun findByProviderAndOAuthId(provider: AuthProvider, oAuthId: String): UserEntity?

    fun findByIdOrNull(id: Long): UserEntity?
}
