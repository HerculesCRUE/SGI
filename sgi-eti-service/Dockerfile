# Start with a base image containing Java runtime
FROM rubensa/openjdk:8-jre-alpine

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Add the application's jar to the container
COPY target/dependency/BOOT-INF/lib /app/lib
COPY target/dependency/META-INF /app/META-INF
COPY target/dependency/BOOT-INF/classes /app

# Run the app
ENTRYPOINT java -Duser.timezone=UTC -Djava.security.egd=file:/dev/./urandom -cp "app:app/lib/*" org.crue.hercules.sgi.eti.EtiApplication