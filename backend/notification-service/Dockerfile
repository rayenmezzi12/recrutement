FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Le context est ./backend, donc pom.xml est à la racine du context
COPY pom.xml ./pom.xml

# Copie tous les modules dont Maven a besoin
COPY eureka-server ./eureka-server
COPY api-gateway ./api-gateway
COPY authentification ./authentification
COPY logique-metier ./logique-metier
COPY chatbotIA ./chatbotIA
COPY notification-service ./notification-service

RUN mvn clean package -DskipTests -pl notification-service -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/notification-service/target/notification-service-*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
