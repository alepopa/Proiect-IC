FROM maven:3.6.0-jdk-8-alpine as build

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build

RUN set -eux -o pipefail && \
    mvn package
    
FROM maven:3.6.0-jdk-8-alpine

COPY --from=build /build/target/ticket-checker-server.jar /app/
COPY src/main/resources /app/config/
COPY public /app/public/

WORKDIR /app

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "ticket-checker-server.jar" ]