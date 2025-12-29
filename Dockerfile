# ------------ Etapa 1: build (Maven + JDK) ------------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copia arquivos de build e dependências primeiro para aproveitar cache
COPY pom.xml .
COPY src ./src

# Compila o projeto (gera o JAR no target/)
RUN ./mvnw -q -DskipTests clean package || \
    (apt-get update && apt-get install -y maven && mvn -q -DskipTests clean package)

# ------------ Etapa 2: runtime (JRE leve) ------------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Render define a porta via variável PORT; Spring Boot usa fallback 8080
ENV PORT=8080
EXPOSE 8080

# Copia o JAR gerado da etapa de build
# >>> ajuste este nome se o seu JAR final tiver outro nome <<<
COPY --from=build /app/target/rolos-monitor-0.1.0.jar app.jar

# Healthcheck simples (opcional)
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s \
  CMD wget -qO- http://localhost:${PORT}/actuator/health || exit 1

# Comando de inicialização
CMD ["java", "-jar", "app.jar"]
