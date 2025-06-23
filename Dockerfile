# Usa una imagen base con JDK 11
FROM maven:3.8.3-eclipse-temurin-11 AS build

COPY ./pom.xml /usr/src/app/pom.xml
# Directorio de trabajo dentro del contenedor

RUN mvn --file /usr/src/app/pom.xml -B install -e -DskipTests=true

COPY . /usr/src/app
RUN mvn --file /usr/src/app/pom.xml -B clean package -DskipTests=true

FROM eclipse-temurin:11-jdk

COPY --from=build /usr/src/app/target/login-api.jar /usr/src/app/login-api.jar

# Expone el puerto que usa Spring Boot
EXPOSE 8080

# Comando para ejecutar la app

ENTRYPOINT ["java", "-jar", "/usr/src/app/login-api.jar"]
