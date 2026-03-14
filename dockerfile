FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

<<<<<<< HEAD
CMD ["java","-jar","target/portal-0.0.1-SNAPSHOT.jar"]
=======
CMD ["java","-jar","target/portal-0.0.1-SNAPSHOT.jar"]
>>>>>>> e35e0134ea21145a26737ae3022a4ff3d858a668
