FROM openjdk:18-alpine
MAINTAINER Joao Moraes
WORKDIR /usr/app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

#./gradlew bootJar
#docker build -t katalogize/katalogize-app .