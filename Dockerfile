# ──────────────────────────────────────────────────────────
# Stage 1: Dependências (camada cacheável separada do código)
# ──────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS deps
WORKDIR /app
# Copia APENAS o pom.xml — só invalida esta camada se mudar dependências
COPY pom.xml .
RUN mvn dependency:go-offline -q

# ──────────────────────────────────────────────────────────
# Stage 2: Build (re-usa camada de deps do cache)
# ──────────────────────────────────────────────────────────
FROM deps AS build
COPY src ./src
# -T 1C: usa 1 thread por CPU; -q silencia output desnecessário
RUN mvn package -DskipTests -T 1C -q

# ──────────────────────────────────────────────────────────
# Stage 3: Runtime mínimo
# ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/quarkus-app/ /app/quarkus-app/
EXPOSE 8081
# Reduz latência de startup da JVM: desativa verificação de bytecode (JVM JIT já valida)
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Xms64m -Xmx256m -XX:TieredStopAtLevel=1"
CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]
