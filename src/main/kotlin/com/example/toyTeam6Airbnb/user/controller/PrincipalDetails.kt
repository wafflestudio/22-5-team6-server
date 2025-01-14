package com.example.toyTeam6Airbnb.user.controller

import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class PrincipalDetails(
    private val user: UserEntity,
    private val attributes: MutableMap<String, Any> = mutableMapOf()
) : UserDetails, OAuth2User {

    fun getUser(): UserEntity {
        return user
    }

    fun getId(): Long {
        return user.id!!
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return attributes
    }

    override fun getName(): String {
        return user.username
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(GrantedAuthority { user.username })
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
