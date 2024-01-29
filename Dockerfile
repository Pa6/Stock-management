# Use an official OpenJDK runtime as a parent image

FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp

ARG JAR_FILE=target/*.jar

# Copy the application JAR file into the container
COPY ./target/product_management_service-0.0.1-SNAPSHOT.jar /app.jar

# Specify the command to run your application
CMD ["java", "-jar", "app.jar"]
