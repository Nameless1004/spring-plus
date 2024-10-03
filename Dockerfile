FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]