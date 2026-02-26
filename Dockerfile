FROM gradle:8.8-jdk17 AS builder
WORKDIR /home/app
COPY --chown=gradle:gradle . /home/app
RUN gradle clean bootJar -x test

FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
ARG JAR_FILE=/home/app/build/libs/sfsto-mvp-0.1.0.jar
RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*
COPY --from=builder ${JAR_FILE} app.jar
COPY wait-for-db.sh /wait-for-db.sh
RUN chmod +x /wait-for-db.sh
ENTRYPOINT ["/bin/bash","-lc","/wait-for-db.sh && java -jar /app.jar"]
