FROM eclipse-temurin:17
WORKDIR /app

COPY target/client-1.0-SNAPSHOT.jar /app/client-1.0-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "client-1.0-SNAPSHOT.jar"]
CMD ["--help"]