FROM maven:3.6.0-jdk-8-alpine as build

COPY . /build

WORKDIR /build

RUN set -eux -o pipefail && \
    mvn package
    
FROM maven:3.6.0-jdk-8-alpine

COPY --from=build /build/target/ticket-checker-server.jar /app/

WORKDIR /app

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "ticket-checker-server.jar" ]