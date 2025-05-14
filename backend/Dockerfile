# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /usr/src/app
COPY . .
RUN mvn package -Dquarkus.package.type=uber-jar -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /usr/src/app/target/*-runner.jar app.jar
EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "java -jar app.jar"]