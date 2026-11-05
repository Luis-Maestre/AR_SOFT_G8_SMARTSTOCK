# --- Compilacion ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# --- Ejecucion ---
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --create-home smartstock
COPY --from=build /app/target/smartstock-backend.jar app.jar
USER smartstock
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
