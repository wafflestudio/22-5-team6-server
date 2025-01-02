FROM amazoncorretto:17
# FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} my-project.jar
# COPY build/libs/*.jar my-project.jar
ENTRYPOINT ["java","-jar","/my-project.jar"]

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime