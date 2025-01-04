package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class PrincipalDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val userEntity = userRepository.findByUsername(username)
        return userEntity?.let { PrincipalDetails(it) }
            ?: throw UsernameNotFoundException("User not found with username: $username")
    }
}
