# syntax=docker/dockerfile:1.4

# -------- BUILD -------- #

FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q -e -DskipTests dependency:go-offline

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B package -DskipTests

# -------- RUNTIME -------- #

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/critical-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]