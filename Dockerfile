FROM amazoncorretto:23-alpine
EXPOSE 3000
COPY target/web-store-*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]