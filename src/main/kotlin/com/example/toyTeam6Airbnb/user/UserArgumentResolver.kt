package com.example.toyTeam6Airbnb.user

import com.example.toyTeam6Airbnb.user.controller.User
import com.example.toyTeam6Airbnb.user.service.UserServiceImpl
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver(
    private val userService: UserServiceImpl
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == User::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): User? {
        return runCatching {
            val accessToken =
                requireNotNull(
                    webRequest.getHeader("Authorization")?.split(" ")?.let {
                        if (it.getOrNull(0) == "Bearer") it.getOrNull(1) else null
                    }
                )
            userService.authenticate(accessToken)
        }.getOrElse {
            if (parameter.hasParameterAnnotation(AuthUser::class.java)) {
                throw AuthenticateException()
            } else {
                null
            }
        }
    }
}
