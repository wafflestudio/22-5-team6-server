package com.example.toyTeam6Airbnb.config

import com.example.toyTeam6Airbnb.user.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ProfileExistenceFilter : OncePerRequestFilter() {

    @Autowired
    @Lazy
    private lateinit var userService: UserService

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication = SecurityContextHolder.getContext().authentication

        // Skip filter for profile upload endpoint
        if (request.requestURI.endsWith("/api/v1/profile") && request.method == "POST") {
            filterChain.doFilter(request, response)
            return
        }

        if (authentication != null && authentication.isAuthenticated) {
            val hasProfile = userService.hasProfile(authentication.name)
            if (!hasProfile) {
                response.status = HttpStatus.FORBIDDEN.value()
                response.contentType = "application/json"
                response.writer.write("{\"error\":\"User with no profile\", \"errorCode\":1009}")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
