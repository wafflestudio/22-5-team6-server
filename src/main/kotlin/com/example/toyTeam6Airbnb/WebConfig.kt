import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig {
    // CORS 설정 중복제거, SecurityConfig에서 다루는 중임
//    @Bean
//    fun corsConfigurer(): WebMvcConfigurer {
//        return object : WebMvcConfigurer {
//            override fun addCorsMappings(registry: CorsRegistry) {
//                registry.addMapping("/**")
//                    .allowedOrigins("*") // 허용할 도메인
//                    .allowedMethods("GET", "POST", "PUT", "DELETE")
//                    .allowedHeaders("*")
//                    .exposedHeaders("Location", "Authorization")
//                    .allowCredentials(true)
//            }
//        }
//    }
}
