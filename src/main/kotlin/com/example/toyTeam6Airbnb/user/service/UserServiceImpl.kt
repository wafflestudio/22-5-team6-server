package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.user.AuthenticateException
import com.example.toyTeam6Airbnb.user.SignInInvalidPasswordException
import com.example.toyTeam6Airbnb.user.SignInUserNotFoundException
import com.example.toyTeam6Airbnb.user.SignUpUsernameConflictException
import com.example.toyTeam6Airbnb.user.UserAccessTokenUtil
import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override fun signUp(
        username: String,
        password: String
    ): User {
        if (userRepository.existsByUsername(username)) {
            throw SignUpUsernameConflictException()
        }

        val encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

        val user =
            userRepository.save(
                UserEntity(
                    username = username,
                    password = encryptedPassword,
                    kakaoId = null,
                    kakaoNickname = null,
                    reservations = emptyList(),
                    rooms = emptyList(),
                    reviews = emptyList()
                )
            )
        return User.fromEntity(user)
    }

    override fun signIn(
        username: String,
        password: String
    ): Pair<User, String> {
        val targetUser = userRepository.findByUsername(username) ?: throw SignInUserNotFoundException()
        if (!BCrypt.checkpw(password, targetUser.password)) {
            throw SignInInvalidPasswordException()
        }
        val accessToken = UserAccessTokenUtil.generateAccessToken(targetUser.id!!)
        return Pair(User.fromEntity(targetUser), accessToken)
    }

    override fun authenticate(accessToken: String): User {
        val userId = UserAccessTokenUtil.validateAccessTokenGetUserId(accessToken) ?: throw AuthenticateException()
        val user = userRepository.findByIdOrNull(userId) ?: throw AuthenticateException()
        return User.fromEntity(user)
    }
}
