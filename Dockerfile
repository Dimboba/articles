FROM gradle:8.10.0-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src
RUN gradle build -x test
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
