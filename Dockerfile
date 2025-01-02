FROM amazoncorretto:17
# FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.war

COPY ${JAR_FILE} my-project.war
# COPY build/libs/*.jar my-project.jar
ENTRYPOINT ["java","-jar","/my-project.war"]

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime