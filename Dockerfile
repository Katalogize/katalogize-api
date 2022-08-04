FROM openjdk:18-alpine
MAINTAINER Joao Moraes
WORKDIR /katalogize-api
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /katalogize-api/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

#./gradlew bootJar
#docker build -t katalogize/katalogize-app .