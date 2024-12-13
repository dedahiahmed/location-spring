FROM maven AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ src/
RUN mvn clean package -B -DskipTests -X

FROM openjdk:17
COPY --from=builder /app/target/*.jar location-backend.jar

ENTRYPOINT ["java", "-jar", "/location-backend.jar"]