# Usa una imagen oficial de OpenJDK 17
FROM eclipse-temurin:17-jdk

# Setea el working directory
WORKDIR /app

# Copia el pom.xml y descarga dependencias primero (cache efectivo)
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Copia el resto del proyecto
COPY . .

# Empaqueta la aplicación (sin tests para producción)
RUN ./mvnw clean package -DskipTests

# Expone el puerto (ajusta si usas otro)
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/*.jar"]
