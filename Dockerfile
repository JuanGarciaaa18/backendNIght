# -----------------------------------------------------------
# FASE 1: BUILD (CONSTRUCCIÓN DE LA APLICACIÓN)
# En esta fase, compilamos y empaquetamos nuestra aplicación Spring Boot.
# -----------------------------------------------------------

# Usamos una imagen base de OpenJDK 21 JDK.
FROM openjdk:21-jdk AS build # CAMBIO: de 24-jdk a 21-jdk

# Establecemos el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copia el archivo pom.xml primero para que Maven pueda descargar dependencias en una capa separada.
COPY pom.xml .

# Actualizamos los paquetes del sistema y instalamos Maven.
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

# CAMBIO CLAVE AQUÍ: Usamos la imagen JRE 21 'slim-buster' que SÍ está disponible.
FROM openjdk:21-jre-slim-buster # CAMBIO: de 24-jre a 21-jre-slim-buster

# Establece el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Expone el puerto por defecto de Spring Boot (8080).
EXPOSE 8080

# Copia el JAR ejecutable de la fase de construcción a la fase de ejecución.
COPY --from=build /app/target/*.jar app.jar

# Define el comando para ejecutar la aplicación.
# Si tu aplicación usaba características preview de Java 24 y ahora usas Java 21,
# verifica si aún necesitas --enable-preview (es menos común para J21 que para J24/J23).
ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "app.jar"]

# Si es necesario mantener --enable-preview (menos común en J21 a menos que uses características muy específicas):
# ENTRYPOINT ["java", "--enable-preview", "-Dserver.port=8080", "-jar", "app.jar"]