# stage 1: build
FROM maven:3.9.11-eclipse-temurin-25 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# stage 2: run
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8002
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
