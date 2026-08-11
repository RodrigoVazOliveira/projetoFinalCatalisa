# Configuração de Gerenciamento de Cores da JVM em Container

## 📋 Overview

Este documento descreve como a JVM foi configurada para RESPEITAR os limites de CPU e memória do container. Sem essa configuração, a JVM pode tentar usar mais recursos do que está disponível, causando problemas de performance e crashes.

## 🔧 Configurações Aplicadas

### 1. **Dockerfile - Variáveis de Ambiente JVM**

As seguintes flags foram adicionadas ao `JAVA_OPTS`:

```dockerfile
-XX:+UseContainerSupport
```
- **Ativa a detecção automática de container**
- Permite que a JVM respeite os limites de memória e CPU do Docker/Podman

```dockerfile
-XX:MaxRAMPercentage=75.0
```
- **Heap máximo = 75% da memória do container**
- Deixa 25% para overhead da JVM (metaspace, threads, etc.)

```dockerfile
-XX:MinRAMPercentage=50.0
```
- **Heap inicial = 50% da memória do container**
- Garante alocação de memória suficiente desde o início

```dockerfile
-XX:+UseG1GC
```
- **Usa o Garbage Collector G1GC**
- Otimizado para containers e aplicações com baixa latência

```dockerfile
-XX:MaxGCPauseMillis=200
```
- **Limita pausas do garbage collection a 200ms**
- Melhora responsividade da aplicação

```dockerfile
-XX:+ParallelRefProcEnabled
-XX:+UnlockDiagnosticVMOptions
-XX:G1SummarizeRSetStatsPeriod=1
```
- **Otimizações adicionais de GC**
- Melhora performance em ambientes containerizados

## 📊 Docker Compose - Limites de Recursos

No `docker-compose.yml`, foi adicionada a seção `deploy.resources`:

```yaml
deploy:
  resources:
    limits:
      cpus: '2'           # Máximo 2 CPUs
      memory: 1536M       # Máximo 1536MB de RAM
    reservations:
      cpus: '1'           # Reservar 1 CPU
      memory: 1024M       # Reservar 1GB de RAM
```

### Explicação:

- **limits.cpus**: Limite máximo de CPUs que o container pode usar
- **limits.memory**: Limite máximo de memória RAM
- **reservations.cpus**: CPU reservada (garantida)
- **reservations.memory**: Memória reservada (garantida)

## 🎯 Como Funciona

1. O container inicia com os limites definidos
2. A JVM detecta esses limites com `UseContainerSupport`
3. A JVM aloca heap: `1536M * 0.75 = 1152M`
4. Deixa espaço para: threads, metaspace, buffers: `1536M * 0.25 = 384M`
5. GC G1 monitora e ajusta alocações em tempo real

## 🔄 Customização

Para ambientes diferentes, ajuste os valores:

### Desenvolvimento (recursos limitados):
```yaml
deploy:
  resources:
    limits:
      cpus: '1'
      memory: 512M
    reservations:
      cpus: '0.5'
      memory: 256M
```

### Produção (alta disponibilidade):
```yaml
deploy:
  resources:
    limits:
      cpus: '4'
      memory: 4096M
    reservations:
      cpus: '2'
      memory: 2048M
```

## 📈 Monitoramento

Para verificar se a JVM está respeitando os limites:

```bash
# Entrar no container
docker exec -it zupayments-app bash

# Verificar informações da JVM
jcmd 1 VM.system_properties | grep -E "java.vm|java.runtime|user.dir"

# Verificar uso de memória
jcmd 1 GC.heap_info

# Verificar status do GC
jcmd 1 GC.run
```

## ⚠️ Problemas Comuns

### 1. **OutOfMemoryError**
- **Causa**: Heap alocado é menor que o necessário
- **Solução**: Aumentar `memory` no docker-compose.yml ou reduzir `MaxRAMPercentage`

### 2. **Muitas Pausas de GC**
- **Causa**: Heap muito pequeno ou muita concorrência
- **Solução**: Aumentar memória ou ajustar `MaxGCPauseMillis`

### 3. **Aplicação Lenta**
- **Causa**: CPU limitada ou GC agressivo
- **Solução**: Aumentar `cpus` no docker-compose.yml

## 🚀 Referências

- [Docker Docs - Resource Constraints](https://docs.docker.com/config/containers/resource_constraints/)
- [Java 21 - Container Awareness](https://docs.oracle.com/en/java/javase/21/docs/api/)
- [G1GC Tuning](https://www.oracle.com/technical-resources/articles/java/g1gc.html)

## ✅ Verificação

Para confirmar que as configurações estão funcionando:

```bash
# Build e start containers
docker-compose up --build

# Verificar logs da aplicação
docker logs -f zupayments-app

# Você deve ver mensagens indicando que a JVM detectou o container
```

Procure por linhas como:
- `OpenJDK 64-Bit Server VM`
- Linhas de inicialização com as flags JVM
- Confirmação que a aplicação iniciou corretamente

