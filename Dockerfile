# BUILD stage
FROM maven:3.6.0-jdk-8 AS build
RUN mkdir -p /tmp/app
ADD pom.xml /tmp/app
ADD src /tmp/app/src

# build artefact
WORKDIR /tmp/app/
RUN mvn clean install -DskipTests=true

# RUN stage
FROM maven:3.6.0-jdk-8

RUN mkdir /app
COPY --from=build /tmp/app/target/debt-model-0.0.1-SNAPSHOT.jar /app/debt-model-api.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/debt-model-api.jar"]