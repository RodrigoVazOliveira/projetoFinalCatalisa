#!/bin/bash
# Script para gerar keystore para HTTPS + HTTP/2

set -e

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}🔐 Gerador de Keystore para HTTPS + HTTP/2${NC}"
echo "═══════════════════════════════════════════════════════════"
echo ""

# Verificar se keytool está disponível
if ! command -v keytool &> /dev/null; then
    echo -e "${RED}❌ keytool não encontrado${NC}"
    echo "   Instale Java JDK para ter acesso ao keytool"
    exit 1
fi

# Solicitar informações
echo -e "${BLUE}Informações do Certificado:${NC}"
echo ""

read -p "Nome do país (ex: BR): " COUNTRY
read -p "Estado (ex: SP): " STATE
read -p "Cidade (ex: São Paulo): " CITY
read -p "Organização (ex: ZUPayments): " ORG
read -p "Seu nome (CN): " NAME
read -p "Alias do certificado (ex: http2-alias) [http2-alias]: " ALIAS
ALIAS=${ALIAS:-http2-alias}

read -sp "Senha do keystore: " KEYSTORE_PASS
echo ""
read -sp "Confirme a senha: " KEYSTORE_PASS_CONFIRM
echo ""

if [ "$KEYSTORE_PASS" != "$KEYSTORE_PASS_CONFIRM" ]; then
    echo -e "${RED}❌ Senhas não coincidem!${NC}"
    exit 1
fi

# Diretório do projeto
PROJECT_DIR="/home/rodrigo/IdeaProjects/projetoFinalCatalisa"
KEYSTORE_FILE="$PROJECT_DIR/keystore.p12"
KEYSTORE_RESOURCE="$PROJECT_DIR/src/main/resources/keystore.p12"

echo ""
echo -e "${BLUE}Gerando keystore...${NC}"

# Gerar keystore
keytool -genkeypair \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -keystore "$KEYSTORE_FILE" \
  -storetype PKCS12 \
  -storepass "$KEYSTORE_PASS" \
  -validity 365 \
  -dname "CN=$NAME,OU=$ORG,L=$CITY,ST=$STATE,C=$COUNTRY" \
  -noprompt

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Keystore gerado com sucesso!${NC}"
else
    echo -e "${RED}❌ Erro ao gerar keystore${NC}"
    exit 1
fi

# Copiar para resources
echo ""
echo -e "${BLUE}Copiando keystore para recursos...${NC}"

cp "$KEYSTORE_FILE" "$KEYSTORE_RESOURCE"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Keystore copiado!${NC}"
else
    echo -e "${RED}❌ Erro ao copiar keystore${NC}"
    exit 1
fi

# Exibir informações
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${GREEN}✅ Keystore criado com sucesso!${NC}"
echo ""
echo -e "${YELLOW}📋 Informações:${NC}"
echo "   Arquivo: $KEYSTORE_RESOURCE"
echo "   Alias: $ALIAS"
echo "   Tipo: PKCS12"
echo "   Validade: 365 dias"
echo ""
echo -e "${YELLOW}🔐 Configuração para application.properties:${NC}"
echo ""
echo "server.port=8443"
echo "server.ssl.key-store=classpath:keystore.p12"
echo "server.ssl.key-store-password=$KEYSTORE_PASS"
echo "server.ssl.key-store-type=PKCS12"
echo "server.ssl.key-alias=$ALIAS"
echo "server.http2.enabled=true"
echo "server.compression.enabled=true"
echo ""
echo -e "${YELLOW}⚠️  IMPORTANTE:${NC}"
echo "   • Não versione keystore.p12 no Git!"
echo "   • Use senhas fortes em produção"
echo "   • Este é um certificado auto-assinado (desenvolvimento)"
echo "   • Para produção, use certificado de CA confiável"
echo ""
echo -e "${YELLOW}🧪 Para testar com HTTPS:${NC}"
echo ""
echo "mvn spring-boot:run"
echo "curl -I --insecure --http2 https://localhost:8443/api/usuarios"
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"

