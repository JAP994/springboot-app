# Usamos la imagen oficial de Eclipse Temurin con OpenJDK 21
FROM eclipse-temurin:21-jdk

# Instalamos Maven
RUN apt-get update && apt-get install -y maven && apt-get clean

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo pom.xml primero para aprovechar cache de Docker
COPY pom.xml .

# Descargamos dependencias (cache)
RUN mvn dependency:go-offline

# Copiamos todo el código fuente
COPY src ./src

# Por defecto ejecutamos spring-boot:run (lo puedes sobrescribir en docker-compose)
CMD ["mvn", "spring-boot:run"]
