FROM ghcr.io/graalvm/native-image:latest AS builder

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml
COPY src src

RUN ./mvnw -B -Pnative native:compile --file pom.xml

FROM alpine:3

RUN apk update
RUN apk upgrade
RUN apk add gcompat libstdc++

EXPOSE 8080

WORKDIR /app

COPY --from=builder /workspace/target/jid /app/jid

CMD ["/app/jid"]
