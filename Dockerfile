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

# Configurações de JVM para containers
# Ativa suporte a container, respeita limites de recursos
# MaxRAMPercentage: usa até 75% da memória disponível do container
# MinRAMPercentage: garante heap inicial apropriado
# UseG1GC: usa garbage collector G1 (otimizado para containers)
# MaxGCPauseMillis: limita pausa do GC a 200ms
ENV JAVA_OPTS="-XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:MinRAMPercentage=50.0 \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+ParallelRefProcEnabled \
  -XX:+UnlockDiagnosticVMOptions \
  -XX:G1SummarizeRSetStatsPeriod=1"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Executar aplicação
# -XX:ActiveProcessorCount pode ser definido via docker-compose com limits
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

