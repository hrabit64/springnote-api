ENV TZ=Asia/Seoul
FROM openjdk:17-jdk-slim as runner
WORKDIR /app
COPY ./libs/*.jar app.jar
EXPOSE 8080
RUN mkdir ./images

ENTRYPOINT ["java","-Dspring.profiles.active=product","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]