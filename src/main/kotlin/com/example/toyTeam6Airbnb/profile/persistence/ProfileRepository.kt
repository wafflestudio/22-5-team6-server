package com.example.toyTeam6Airbnb.profile.persistence

import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepository : JpaRepository<ProfileEntity, Long> {
    fun findByUser(user: UserEntity): ProfileEntity?

    fun findByUserId(userId: Long): ProfileEntity?

    fun existsByUser(user: UserEntity): Boolean
}
