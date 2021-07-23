FROM adoptopenjdk/maven-openjdk11 AS build

WORKDIR /build

COPY pom.xml .
COPY src/ src/

RUN mvn package -DskipTests=true

FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine-slim AS release

COPY --from=build /build/target/*.jar /app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
