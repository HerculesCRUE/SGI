# Start with a base image containing Java runtime
FROM eclipse-temurin:8-jre-alpine

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's entry point
ARG JAVA_MAIN_CLASS
ENV JAVA_MAIN_CLASS=$JAVA_MAIN_CLASS

# Add the application's jar to the container
COPY target/dependency/BOOT-INF/lib /app/lib
COPY target/dependency/META-INF /app/META-INF
COPY target/dependency/BOOT-INF/classes /app

# Run the app
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -cp "app:app/lib/*" $JAVA_MAIN_CLASS
