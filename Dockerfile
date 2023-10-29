FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine

COPY --from=builder /workspace/app/target/*.jar /app/jid-server.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/jid-server.jar"]
