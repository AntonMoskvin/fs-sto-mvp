FROM gradle:8.8-jdk17 AS builder
WORKDIR /home/app
COPY --chown=gradle:gradle . /home/app
RUN gradle clean bootJar -x test

FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
ARG JAR_FILE=/home/app/build/libs/sfsto-mvp-0.1.0.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
