FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /workspace

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /workspace

COPY --from=builder /workspace/target/spothook-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
