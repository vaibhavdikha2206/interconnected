FROM openjdk:18-jdk-slim
WORKDIR /app
COPY target/Docker-Jenkins-Integration-Interconnected.jar /app/Docker-Jenkins-Integration-Interconnected.jar
ENTRYPOINT ["java", "-jar", "Docker-Jenkins-Integration-Interconnected.jar"]