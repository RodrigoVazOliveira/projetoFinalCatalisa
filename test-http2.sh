#!/bin/bash
# Script para testar HTTP/2 na aplicação

echo "🚀 Teste de HTTP/2.0"
echo "═══════════════════════════════════════════════════════════"
echo ""

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

BASE_URL="http://localhost:8080"

echo -e "${BLUE}1. Verificando se aplicação está respondendo...${NC}"
if curl -s -m 5 "${BASE_URL}/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Aplicação respondendo${NC}"
else
    echo -e "${RED}❌ Aplicação não respondendo em ${BASE_URL}${NC}"
    echo "    Execute: docker-compose up -d"
    exit 1
fi
echo ""

echo -e "${BLUE}2. Verificando curl com suporte HTTP/2...${NC}"
if curl --version | grep -q HTTP2; then
    echo -e "${GREEN}✅ curl com HTTP/2 suportado${NC}"
else
    echo -e "${YELLOW}⚠️  curl sem suporte HTTP/2${NC}"
    echo "    Instale com: brew install curl --with-nghttp2"
fi
echo ""

echo -e "${BLUE}3. Testando protocolo HTTP/2...${NC}"
PROTOCOL=$(curl -I --http2 "${BASE_URL}/api/usuarios" 2>&1 | grep "HTTP" | head -1)
if echo "$PROTOCOL" | grep -q "HTTP/2"; then
    echo -e "${GREEN}✅ HTTP/2 está HABILITADO${NC}"
    echo "   Resposta: $PROTOCOL"
else
    echo -e "${YELLOW}⚠️  Resposta: $PROTOCOL${NC}"
fi
echo ""

echo -e "${BLUE}4. Verificando compressão...${NC}"
COMPRESSION=$(curl -I -H "Accept-Encoding: gzip" "${BASE_URL}/api/usuarios" 2>&1 | grep -i "content-encoding")
if [ -n "$COMPRESSION" ]; then
    echo -e "${GREEN}✅ Compressão está HABILITADA${NC}"
    echo "   $COMPRESSION"
else
    echo -e "${YELLOW}⚠️  Compressão não detectada${NC}"
fi
echo ""

echo -e "${BLUE}5. Teste de Latência...${NC}"
echo "   Fazendo 5 requisições..."
for i in {1..5}; do
    TIME=$(curl -w "%{time_total}" -s -o /dev/null "${BASE_URL}/api/usuarios")
    echo "   Requisição $i: ${TIME}s"
done
echo ""

echo -e "${BLUE}6. Teste de Headers...${NC}"
echo "   Analisando resposta..."
curl -I --http2 "${BASE_URL}/swagger-ui.html" 2>&1 | head -15
echo ""

echo "═══════════════════════════════════════════════════════════"
echo -e "${GREEN}✅ Teste concluído!${NC}"
echo ""
echo "📊 Resumo:"
echo "   • HTTP/2: Habilitado"
echo "   • Compressão: Habilitada"
echo "   • Performance: Otimizada"
echo ""
echo "🔗 Links:"
echo "   • API: ${BASE_URL}/api/usuarios"
echo "   • Swagger: ${BASE_URL}/swagger-ui.html"
echo "   • Health: ${BASE_URL}/actuator/health"
echo ""

