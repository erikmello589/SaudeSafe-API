#Construindo imagem para a aplicação (Spring Boot)

FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

COPY . . 

RUN apt-get install maven -y
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

EXPOSE 8080

WORKDIR /saudesafeapp

COPY  --from=build /target/saudesafe-0.0.1-SNAPSHOT.jar /saudesafeapp/saudesafespring-app.jar 

ENTRYPOINT [ "java", "-jar", "saudesafespring-app.jar" ]