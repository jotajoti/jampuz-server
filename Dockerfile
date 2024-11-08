FROM eclipse-temurin:21-jre-alpine

RUN apk --no-cache add curl

COPY target/*.jar /app/jampuz-server.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/jampuz-server.jar"]
