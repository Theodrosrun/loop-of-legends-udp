FROM eclipse-temurin:17

WORKDIR /app

COPY target/server-1.0-SNAPSHOT.jar /app/server-1.0-SNAPSHOT.jar

EXPOSE 10000
EXPOSE 20000
EXPOSE 20001

ENTRYPOINT ["java", "-jar", "server-1.0-SNAPSHOT.jar"]