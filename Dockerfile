FROM openjdk:17

ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /app

COPY /target/my-gateway*.jar /app/my-gateway.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/my-gateway.jar"]