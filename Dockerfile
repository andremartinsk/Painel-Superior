
# Usar imagem base com Java 17 (compatível com Spring Boot)
FROM openjdk:17-jdk-slim

# Definir diretório de trabalho dentro do container
WORKDIR /app

# Copiar o JAR gerado para dentro do container
COPY target/rolos-monitor-0.1.0.jar app.jar

# Expor a porta (Render define dinamicamente, mas isso ajuda localmente)
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "app.jar"]

