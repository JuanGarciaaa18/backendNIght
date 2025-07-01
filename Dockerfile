# -----------------------------------------------------------
# FASE 1: BUILD (CONSTRUCCIÓN DE LA APLICACIÓN)
# En esta fase, compilamos y empaquetamos nuestra aplicación Spring Boot.
# -----------------------------------------------------------

# Usamos una imagen base de Ubuntu con la versión más reciente, adecuada para construir.
# Le damos un alias 'build' para referenciarla en etapas posteriores.
FROM ubuntu:latest AS build

# Actualizamos los paquetes del sistema y instalamos OpenJDK 21 JDK.
# El '-y' es para confirmar automáticamente la instalación.
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk

# Establecemos el directorio de trabajo dentro del contenedor.
# Aquí es donde se copiará el código de tu proyecto.
WORKDIR /app

# Copiamos todo el contenido del directorio local (donde está el Dockerfile)
# al directorio de trabajo en el contenedor.
# El '..' representa el directorio padre, asumiendo que tu Dockerfile está
# en la raíz del proyecto y quieres copiar todo el proyecto.
COPY . .

# Ejecutamos el comando Maven para construir el proyecto y crear el JAR ejecutable.
# 'clean install' limpia y construye el proyecto.
# '-DskipTests' omite la ejecución de tests para una construcción más rápida.
# Asegúrate de que tienes 'mvnw' (Maven Wrapper) en tu proyecto, o usa 'mvn' si lo tienes instalado globalmente.
# Si solo usas 'mvn' sin 'mvnw', necesitarías instalar Maven en esta fase.
# Pero dado que la imagen base es 'ubuntu:latest', no viene con Maven preinstalado.
# Por lo tanto, el uso de 'mvnw' es lo más práctico aquí.
# Si no tienes mvnw, la línea debería ser: RUN apt-get install -y maven && mvn clean install -DskipTests
RUN ./mvnw clean install -DskipTests

# -----------------------------------------------------------
# FASE 2: RUN (EJECUCIÓN DE LA APLICACIÓN)
# En esta fase, creamos una imagen más ligera solo con lo necesario para ejecutar el JAR.
# -----------------------------------------------------------

# Usamos una imagen base más ligera que solo contiene el JRE (Java Runtime Environment).
# Esto reduce significativamente el tamaño final de la imagen Docker.
FROM openjdk:21-jre-slim

# Expone el puerto por defecto de tu aplicación Spring Boot.
# Render (o Heroku) remapeará este puerto al puerto público necesario.
EXPOSE 8080

# Copiamos el archivo JAR compilado desde la fase 'build'.
# La ruta '/app/target/*.jar' asume que Maven coloca el JAR en 'target/'
# y usa el patrón comodín para el nombre del JAR.
# El JAR se renombrará a 'app.jar' para simplificar el comando de ejecución.
COPY --from=build /app/target/*.jar app.jar

# Define el comando de entrada que se ejecutará cuando el contenedor inicie.
# '-Dserver.port=8080' le dice a Spring Boot que escuche en el puerto 8080.
# Render se encargará de mapear su puerto dinámico al puerto 8080 del contenedor.
ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "app.jar"]

# Si usas perfiles de Spring, y quieres activar el perfil 'prod', puedes hacer:
# ENTRYPOINT ["java", "-Dserver.port=8080", "-Dspring.profiles.active=prod", "-jar", "app.jar"]