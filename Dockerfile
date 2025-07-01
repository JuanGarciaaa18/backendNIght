# -----------------------------------------------------------
# FASE 1: BUILD (CONSTRUCCIÓN DE LA APLICACIÓN)
# En esta fase, compilamos y empaquetamos nuestra aplicación Spring Boot.
# -----------------------------------------------------------

# Usamos una imagen base de Maven con OpenJDK 24.
# Es la forma más robusta de asegurar que Maven y el JDK estén disponibles.
# Verificar en Docker Hub si 'maven:3.9.5-eclipse-temurin-24' o similar está disponible.
# Si no lo está, usaremos 'openjdk:24-jdk' y instalaremos Maven.
FROM openjdk:24-jdk AS build

# Establecemos el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copia el archivo pom.xml primero para que Maven pueda descargar dependencias en una capa separada.
# Esto acelera las reconstrucciones si solo cambian los archivos fuente.
COPY pom.xml .

# Descarga las dependencias del proyecto.
# Ya que estamos en openjdk:24-jdk, Maven no está preinstalado. Lo instalamos.
# Luego ejecutamos 'go-offline' para precargar las dependencias.
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:go-offline

# Copia todo el código fuente del proyecto.
COPY src ./src

# Empaqueta la aplicación Spring Boot en un JAR ejecutable.
# Usamos 'mvn' directamente ya que lo instalamos.
# '-DskipTests' omite la ejecución de tests.
# Si tu aplicación usa características preview en la compilación que NO se ejecutan en runtime,
# puedes omitir '--enable-preview' aquí si ya está en tu pom.xml y Maven lo maneja.
# Si tu IDE/Maven Compiler Plugin lo gestiona, no es estrictamente necesario aquí.
RUN mvn clean install -DskipTests

# -----------------------------------------------------------
# FASE 2: RUN (EJECUCIÓN DE LA APLICACIÓN)
# En esta fase, creamos una imagen más ligera solo con lo necesario para ejecutar el JAR.
# -----------------------------------------------------------

# Usamos una imagen base más ligera que solo contiene el JRE (Java Runtime Environment) 24.
# Verificar en Docker Hub si 'openjdk:24-jre-slim-buster' o 'eclipse-temurin:24-jre-alpine' están disponibles.
# 'slim-buster' es una buena opción por su tamaño y compatibilidad.
FROM openjdk:24-jre-slim-buster

# Establece el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Expone el puerto por defecto de Spring Boot (8080).
EXPOSE 8080

# Copia el JAR ejecutable de la fase de construcción a la fase de ejecución.
# El JAR suele tener el formato backendNIght-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/*.jar app.jar

# Define el comando para ejecutar la aplicación.
# -Dserver.port=8080 para que Spring Boot escuche en este puerto interno.
# Si tu aplicación usa características preview de Java 24 en TIEMPO DE EJECUCIÓN,
# DEBES añadir '--enable-preview' aquí. Si solo fue para compilación, no es necesario.
ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "app.jar"]

# Ejemplo si necesitas --enable-preview en runtime:
# ENTRYPOINT ["java", "--enable-preview", "-Dserver.port=8080", "-jar", "app.jar"]

# Si tienes un perfil de Spring Boot específico para producción (ej. 'prod'), podrías usar:
# ENTRYPOINT ["java", "-Dserver.port=8080", "-Dspring.profiles.active=prod", "-jar", "app.jar"]