FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80", "-XX:InitialRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
