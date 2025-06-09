FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw install -DskipTests

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

# Create user to run the application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Application variables
ARG JAR_FILE=/workspace/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget --quiet --tries=1 --spider http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]