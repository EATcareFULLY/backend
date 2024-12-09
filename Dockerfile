FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .

#keep local copy of dependencies for faster start
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-slim

#istall libraries crucial for opencv and tesseract OCR to work, clear apt cache for smaller image size
RUN apt-get update && apt-get install -y \
    libstdc++6 \
    libtesseract-dev \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/*.jar eat-carefully.jar
ENTRYPOINT ["java", "-jar", "eat-carefully.jar"]