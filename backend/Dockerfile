FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src/ src/
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
RUN useradd --system --uid 10001 chari
COPY --from=build /workspace/target/chari-app-0.0.1-SNAPSHOT.jar app.jar

USER chari
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
