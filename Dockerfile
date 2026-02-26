# ===== ETAPA 1: Compilar el proyecto =====
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven primero
# (esto aprovecha el caché de Docker si el pom.xml no cambió)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# ===== ETAPA 2: Ejecutar el proyecto =====
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar solo el JAR generado en la etapa anterior
COPY --from=build /app/target/shopweb-0.0.1-SNAPSHOT.jar app.jar

# Puerto que expone el contenedor
EXPOSE 8080

# Comando para arrancar la app
ENTRYPOINT ["java", "-jar", "app.jar"]