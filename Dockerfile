FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests


FROM amazoncorretto:17-alpine-jdk

COPY --from=builder /app/target/*.jar /eat-carefully.jar

ENTRYPOINT ["java", "-jar", "/eat-carefully.jar"]