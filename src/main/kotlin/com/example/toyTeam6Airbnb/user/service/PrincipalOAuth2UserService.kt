package com.example.toyTeam6Airbnb.user.service

import com.example.toyTeam6Airbnb.user.OAuthException
import com.example.toyTeam6Airbnb.user.controller.PrincipalDetails
import com.example.toyTeam6Airbnb.user.persistence.AuthProvider
import com.example.toyTeam6Airbnb.user.persistence.UserEntity
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class PrincipalOAuth2UserService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        val provider = AuthProvider.from(userRequest.clientRegistration.registrationId)
        val oAuthId = when (provider) {
            AuthProvider.NAVER -> {
                val response = oAuth2User.getAttribute<Map<String, Any>>("response")
                response?.get("id") as? String
            }
            AuthProvider.KAKAO -> oAuth2User.getAttribute<Long>("id")?.toString()
            else -> oAuth2User.getAttribute<String>("sub")
        } ?: throw OAuthException()
        val username = "OAUTH${provider}_$oAuthId"

        val password = passwordEncoder.encode("****")

        val findOne = userRepository.findByProviderAndOAuthId(provider, oAuthId)

        return if (findOne == null) {
            // First time OAuth login / registration
            val user = UserEntity(username = username, password = password, provider = provider, oAuthId = oAuthId)
            userRepository.save(user)
            PrincipalDetails(user, oAuth2User.attributes)
        } else {
            // Member already exists
            PrincipalDetails(findOne, oAuth2User.attributes)
        }
    }
}
