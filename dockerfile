# Use Java 17 image
FROM eclipse-temurin:17-jdk-jammy

# Copy project files
WORKDIR /app
COPY . .

# Build the application
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Run the jar file
CMD ["java", "-jar", "target/*.jar"]