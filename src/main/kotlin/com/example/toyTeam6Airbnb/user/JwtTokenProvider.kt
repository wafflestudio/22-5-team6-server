package com.example.toyTeam6Airbnb.user

import com.example.toyTeam6Airbnb.user.persistence.RefreshTokenEntity
import com.example.toyTeam6Airbnb.user.persistence.RefreshTokenRepository
import com.example.toyTeam6Airbnb.user.persistence.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider(
    @Value("\${spring.security.jwt-secret}")
    private val jwtSecret: String, // Move to configuration
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository
) {
    private val jwtExpirationMs: Long = 3600000L // 1 hour
    private val jwtRefreshExpirationMs: Long = 86400000L // 24 hours

    @Transactional
    fun generateToken(username: String): TokenDto {
        val now = Date()
        val accessToken = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + jwtExpirationMs))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()), SignatureAlgorithm.HS512)
            .compact()
        val refreshToken = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + jwtRefreshExpirationMs))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()), SignatureAlgorithm.HS512)
            .compact()

        val userEntity = userRepository.findByUsername(username)!!
        refreshTokenRepository.deleteByUser(userEntity)
        refreshTokenRepository.flush()
        refreshTokenRepository.save(RefreshTokenEntity(token = refreshToken, user = userEntity))

        return TokenDto(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun validateToken(token: String): Boolean {
        Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseClaimsJws(token)
        return true
    }

    fun getUsernameFromToken(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }
}

data class TokenDto(
    val accessToken: String,
    val refreshToken: String
)
