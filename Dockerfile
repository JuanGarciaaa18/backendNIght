# -----------------------------------------------------------
# FASE 1: BUILD (CONSTRUCCIÓN DE LA APLICACIÓN)
# En esta fase, compilamos y empaquetamos nuestra aplicación Spring Boot.
# -----------------------------------------------------------

# Usamos la imagen oficial de Maven con un JDK de Temurin 21.
# Esta es una versión LTS estable y ampliamente disponible.
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copia el archivo pom.xml primero para que Maven pueda descargar dependencias en una capa separada.
# Esto es una optimización para el cache de Docker.
COPY pom.xml .

# Descarga las dependencias del proyecto. Maven ya está disponible.
RUN mvn dependency:go-offline

# Copia todo el código fuente del proyecto.
COPY src ./src

# Empaqueta la aplicación Spring Boot en un JAR ejecutable.
RUN mvn clean install -DskipTests

# -----------------------------------------------------------
# FASE 2: RUN (EJECUCIÓN DE LA APLICACIÓN)
# En esta fase, creamos una imagen más ligera solo con lo necesario para ejecutar el JAR.
# -----------------------------------------------------------

# Usamos eclipse-temurin:21-jre-alpine. Esta imagen es ligera y muy estable/disponible.
FROM eclipse-temurin:21-jre-alpine

# Establece el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Expone el puerto por defecto de Spring Boot (8080).
EXPOSE 8080

# Copia el JAR ejecutable de la fase de construcción a la fase de ejecución.
COPY --from=build /app/target/*.jar app.jar

# Define el comando para ejecutar la aplicación.
ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "app.jar"]

# Si es necesario mantener --enable-preview (generalmente no para JRE en runtime a menos que se use directamente):
# ENTRYPOINT ["java", "--enable-preview", "-Dserver.port=8080", "-jar", "app.jar"]
