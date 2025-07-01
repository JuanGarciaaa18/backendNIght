# -----------------------------------------------------------
# FASE 1: BUILD (CONSTRUCCIÓN DE LA APLICACIÓN)
# En esta fase, compilamos y empaquetamos nuestra aplicación Spring Boot.
# -----------------------------------------------------------

# Usamos una imagen base de OpenJDK 21 JDK.
# openjdk:21-jdk SÍ debería estar disponible para la fase de build.
FROM openjdk:21-jdk AS build

# Establecemos el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copia el archivo pom.xml primero para que Maven pueda descargar dependencias en una capa separada.
COPY pom.xml .

# Actualizamos los paquetes del sistema e instalamos Maven.
# Luego ejecutamos 'go-offline' para precargar las dependencias.
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:go-offline

# Copia todo el código fuente del proyecto.
COPY src ./src

# Empaqueta la aplicación Spring Boot en un JAR ejecutable.
RUN mvn clean install -DskipTests

# -----------------------------------------------------------
# FASE 2: RUN (EJECUCIÓN DE LA APLICACIÓN)
# En esta fase, creamos una imagen más ligera solo con lo necesario para ejecutar el JAR.
# -----------------------------------------------------------

# CAMBIO CLAVE AQUÍ: Usamos eclipse-temurin:21-jre-alpine.
# Esta es una imagen muy común, ligera y estable.
FROM eclipse-temurin:21-jre-alpine

# Establece el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Expone el puerto por defecto de Spring Boot (8080).
EXPOSE 8080

# Copia el JAR ejecutable de la fase de construcción a la fase de ejecución.
COPY --from=build /app/target/*.jar app.jar

# Define el comando para ejecutar la aplicación.
ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "app.jar"]

# Si es necesario mantener --enable-preview:
# ENTRYPOINT ["java", "--enable-preview", "-Dserver.port=8080", "-jar", "app.jar"]