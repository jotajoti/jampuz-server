FROM eclipse-temurin:21.0.6_7-jre-alpine

RUN apk --no-cache add curl

COPY target/*.jar /app/jampuz-server.jar

EXPOSE 8080

ENTRYPOINT ["java","-Dspring.aot.enabled=true","-jar","/app/jampuz-server.jar"]
