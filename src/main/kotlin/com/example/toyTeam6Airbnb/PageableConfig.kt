package com.example.toyTeam6Airbnb

import org.springframework.context.annotation.Configuration
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class PageableConfig : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        val pageableResolver = PageableHandlerMethodArgumentResolver()
        pageableResolver.setMaxPageSize(100) // 최대 페이지 크기 제한
        resolvers.add(pageableResolver)
    }
}
