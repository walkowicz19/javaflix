# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/quarkus-app/ /app/quarkus-app/
EXPOSE 8081
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0"
CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]
