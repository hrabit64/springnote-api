FROM openjdk:17-jdk-slim
ENV TZ=Asia/Seoul
WORKDIR /app
COPY ./libs/*.jar app.jar
EXPOSE 8080
ENV	USE_PROFILE product

ENTRYPOINT ["java","-Dspring.profiles.active=${USE_PROFILE}","-Duser.timezone=Asia/Seoul","-jar","/app/app.jar"]
