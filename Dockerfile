FROM openjdk:17-jdk-slim
ENV TZ=Asia/Seoul
WORKDIR /app
COPY ./libs/*.jar app.jar
EXPOSE 8080
RUN mkdir ./images

ENTRYPOINT ["java","-Dspring.profiles.active=product","-Duser.timezone=Asia/Seoul","-jar","/app/app.jar"]a","-Dspring.profiles.active=product","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]