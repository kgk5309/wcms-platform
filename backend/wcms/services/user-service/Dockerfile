FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace
COPY . .
RUN ./gradlew :services:user-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/services/user-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
