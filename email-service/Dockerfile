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
HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget --quiet --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Environment variables with defaults
ENV SPRING_PROFILES_ACTIVE=prod
ENV DB_HOST=mysql
ENV DB_PORT=3306
ENV DB_NAME=email_service
ENV DB_USERNAME=email_user
ENV DB_PASSWORD=email_password
ENV EMAIL_HOST=smtp.titan.email
ENV EMAIL_PORT=465
ENV USER_NAME=
ENV EMAIL_PASSWORD=
ENV EUREKA_URI=http://eureka-server:8761/eureka

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]