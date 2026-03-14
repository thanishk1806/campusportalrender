# Use Java 17 image
FROM openjdk:17-jdk-slim

# Copy project files
WORKDIR /app
COPY . .

# Build the application
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Run the jar file
CMD ["java", "-jar", "target/*.jar"]