package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.user.SignUpBadUsernameException
import com.example.toyTeam6Airbnb.user.SignUpUsernameConflictException
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.AuthProvider
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {
    @Transactional
    override fun register(
        username: String,
        password: String
    ): User? {
        if (username.startsWith("OAUTH")) throw SignUpBadUsernameException()
        if (userRepository.existsByUsername(username)) throw SignUpUsernameConflictException()
        val userEntity = UserEntity(
            username = username,
            password = passwordEncoder.encode(password),
            provider = AuthProvider.LOCAL
        ).let {
            userRepository.save(it)
        }
        return User.fromEntity(userEntity)
    }
}
