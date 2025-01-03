package com.example.toyTeam6Airbnb.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.util.Date

object UserAccessTokenUtil {
    fun generateAccessToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + JWT_EXPIRATION_TIME)
        return Jwts.builder()
            .signWith(SECRET_KEY)
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .compact()
    }

    fun validateAccessTokenGetUserId(accessToken: String): String? {
        return try {
            val claims =
                Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(accessToken)
                    .body
            if (claims.expiration < Date()) {
                return null
            }
            return claims.subject
        } catch (e: Exception) {
            null
        }
    }

    private const val JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 10 // 10 days
    private val SECRET_KEY = Keys.hmacShaKeyFor("AASDFASDFASDFASDFASDFASDFASDFASDFASDFSDF".toByteArray(StandardCharsets.UTF_8))
}
