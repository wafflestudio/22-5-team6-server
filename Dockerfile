# Kotlin JAR 빌드
FROM eclipse-temurin:17-jdk

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 결과물 복사
COPY build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
