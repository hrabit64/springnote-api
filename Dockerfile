FROM openjdk:17-jdk-slim as builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootjar

ENV TZ=Asia/Seoul
FROM openjdk:17-jdk-slim as runner
WORKDIR /app
COPY --from=builder build/libs/*.jar app.jar
EXPOSE 8080
RUN mkdir ./images

ENTRYPOINT ["java","-Dspring.profiles.active=product","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]