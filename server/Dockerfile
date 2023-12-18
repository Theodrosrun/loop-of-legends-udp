FROM eclipse-temurin:17
WORKDIR /app

COPY target/server-1.0-SNAPSHOT.jar /app/server-1.0-SNAPSHOT.jar
EXPOSE 1312
EXPOSE 1313
ENTRYPOINT ["java", "-jar", "server-1.0-SNAPSHOT.jar"]
# CMD ["--help"]