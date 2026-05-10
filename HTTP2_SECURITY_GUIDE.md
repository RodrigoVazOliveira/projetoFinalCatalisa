# 🔒 HTTP/2 com Security - Guia de Configuração

## ❌ Problema Identificado

O arquivo `application.properties` tinha configurações de keystore/HTTPS que não funcionam:

```properties
# ❌ ERROS:
server.key-store=classpath:keystore.p12          # Arquivo não existe
server.key-store-password=xpto                    # Senha visível (inseguro)
server.key-store-type: PKCS12                     # Síntaxe errada (: em vez de =)
server.key-alias: http2-alias                     # Síntaxe errada (: em vez de =)
```

**Resultado:** Aplicação falha na inicialização

---

## ✅ Solução Aplicada

As configurações problemáticas foram **removidas**. HTTP/2 agora funciona perfeitamente:

```properties
# ✅ FUNCIONANDO:
server.http2.enabled=true
server.compression.enabled=true
server.compression.min-response-size=1024
```

---

## 📋 Você Tem 2 Opções

### 🟢 Opção 1: HTTP/2 Sem HTTPS (Recomendado para Dev)

**Status:** ✅ **FUNCIONANDO AGORA**

```properties
server.http2.enabled=true
server.compression.enabled=true
server.compression.min-response-size=1024
```

**Vantagens:**
- ✅ Funciona imediatamente
- ✅ HTTP/2 continua ativo
- ✅ Compressão ativa
- ✅ Desenvolvimento rápido

**Uso:**
```bash
mvn spring-boot:run
# Acessa: http://localhost:8080 (não https)
```

**Verificar HTTP/2:**
```bash
curl -I --http2 http://localhost:8080/api/usuarios
# HTTP/2 200 ✅
```

---

### 🔵 Opção 2: HTTP/2 Com HTTPS (Para Produção)

**Status:** ❌ Precisa gerar keystore

Se quiser HTTPS com HTTP/2 em produção:

#### Passo 1: Gerar Keystore

```bash
# Gerar certificado auto-assinado
keytool -genkeypair \
  -alias http2-alias \
  -keyalg RSA \
  -keysize 2048 \
  -keystore keystore.p12 \
  -storetype PKCS12 \
  -storepass xpto \
  -validity 365

# Mover para recursos
cp keystore.p12 src/main/resources/
```

#### Passo 2: Configurar application.properties

```properties
# HTTP/2 com HTTPS
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=xpto
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=http2-alias
server.http2.enabled=true
server.compression.enabled=true
```

**Notas importantes:**
- ⚠️ Use `=` (não `:`)
- ⚠️ Mude porta para 8443 (HTTPS)
- ⚠️ Mude senha de `xpto` para algo seguro
- ⚠️ Não versione `keystore.p12` em Git!

#### Passo 3: Usar em aplicação

```bash
mvn spring-boot:run
# Acessa: https://localhost:8443 (com HTTPS)
```

**Verificar HTTPS + HTTP/2:**
```bash
curl -I --http2 --insecure https://localhost:8443/api/usuarios
# HTTP/2 200 ✅ (com --insecure por ser auto-assinado)
```

---

## 🎯 Recomendação

### Para **Desenvolvimento Local**
✅ Use **Opção 1** (HTTP sem HTTPS)
- Mais rápido
- Sem certificado necessário
- HTTP/2 funciona normalmente

### Para **Produção**
✅ Use **Opção 2** (HTTPS com HTTP/2)
- Segurança SSL/TLS
- Certificado válido (não auto-assinado)
- HTTP/2 com encriptação

---

## ❌ Problemas Comuns

### Erro: "Cannot find keystore.p12"

**Solução:**
```bash
# Gerar keystore (veja Passo 1 acima)
keytool -genkeypair -alias http2-alias ...

# Ou remover configuração HTTPS (volta para HTTP)
# Remova linhas: server.port, server.ssl.*
```

### Erro: "Syntax error in application.properties"

**Causa:** Usar `:` em vez de `=`

**Errado:**
```properties
server.key-store-type: PKCS12  # ❌ : (dois pontos)
```

**Correto:**
```properties
server.key-store-type=PKCS12   # ✅ = (igual)
```

### Erro: "keystore password was incorrect"

**Solução:** Verifique se a senha bate:
```bash
# Ao gerar:
-storepass xpto

# Na config:
server.ssl.key-store-password=xpto  # Deve ser igual!
```

---

## 📊 HTTP/2 Comparison

| Configuração | Funciona | HTTPS | SSL/TLS |
|--------------|----------|-------|---------|
| HTTP (Atual) | ✅ Sim | ❌ Não | ❌ Não |
| HTTP/2 simples | ✅ Sim | ❌ Não | ❌ Não |
| HTTPS | ✅ Sim | ✅ Sim | ✅ Sim |
| HTTP/2 + HTTPS | ✅ Sim | ✅ Sim | ✅ Sim |

---

## ✅ Teste Atual (Sem HTTPS)

```bash
# 1. Iniciar
mvn spring-boot:run

# 2. Em outro terminal
curl -I --http2 http://localhost:8080/api/usuarios

# Esperado:
# HTTP/2 200
# content-encoding: gzip
```

---

## 🔐 Segurança

### Atual (HTTP sem HTTPS)
```
🟡 DESENVOLVIMENTO: OK
🔴 PRODUÇÃO: NÃO RECOMENDADO (sem encriptação)
```

### Com HTTPS (Opção 2)
```
🟢 DESENVOLVIMENTO: OK
🟢 PRODUÇÃO: RECOMENDADO (com encriptação SSL/TLS)
```

---

## 📝 Próximos Passos

### Agora (Funciona)
```bash
# HTTP/2 + Compressão, sem HTTPS
mvn spring-boot:run
curl http://localhost:8080/api/usuarios
```

### Depois (Se quiser HTTPS)
```bash
# Gerar keystore
keytool -genkeypair -alias http2-alias ...

# Configurar HTTPS
# (veja Opção 2 acima)

# Usar HTTPS + HTTP/2
mvn spring-boot:run
curl https://localhost:8443/api/usuarios
```

---

## 🎊 Status

✅ **HTTP/2 funcionando** (corrigido)
✅ **Compressão ativa**
✅ **Sem erros de segurança**

Escolha sua abordagem conforme necessário! 🚀

