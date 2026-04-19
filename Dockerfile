# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /build

# Copiar arquivos do Maven
COPY mvnw .
COPY mvnw.cmd .
x                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   COPY .mvn .mvn
COPY pom.xml .
COPY src/ src/

# Dar permissão de execução ao mvnw
RUN chmod +x ./mvnw

# Build da aplicação
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copiar JAR do stage de build
COPY --from=builder /build/target/*.jar app.jar

# Exposar porta da aplicação
EXPOSE 8080

# Variáveis de ambiente padrão
ENV URL=jdbc:mysql://mysql:3306/zupayments
ENV USER_BANCO_DADOS=root
ENV PASSWORD_BANCO=root
ENV EMAIL=seu-email@gmail.com
ENV SENHA=sua-senha-app
ENV SECRET=sua-secret-jwt
ENV TIMEOUT_LIMIT=3600000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

