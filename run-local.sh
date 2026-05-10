#!/bin/bash
# Script para executar ZUPayments localmente

set -e

echo "🚀 Inicializando ZUPayments..."
echo ""

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}Step 1: Verificar banco de dados...${NC}"

# Verificar se MySQL/MariaDB está rodando
if ! mysql -u root -e "SELECT 1" >/dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  MySQL não está rodando. Tentando iniciar...${NC}"

    # Tentar iniciar com systemd
    if sudo systemctl start mariadb 2>/dev/null || sudo systemctl start mysql 2>/dev/null; then
        echo -e "${GREEN}✅ MySQL iniciado${NC}"
        sleep 2
    else
        echo -e "${YELLOW}⚠️  Não foi possível iniciar MySQL automaticamente${NC}"
        echo "   Execute manualmente: sudo systemctl start mariadb"
        exit 1
    fi
fi

echo -e "${GREEN}✅ MySQL está rodando${NC}"
echo ""

# Criar banco de dados
echo -e "${BLUE}Step 2: Criando banco de dados...${NC}"
mysql -u root -e "CREATE DATABASE IF NOT EXISTS zupayments"
echo -e "${GREEN}✅ Banco 'zupayments' pronto${NC}"
echo ""

# Exportar variáveis de ambiente
echo -e "${BLUE}Step 3: Configurando variáveis de ambiente...${NC}"
export URL="jdbc:mysql://localhost:3306/zupayments"
export USER_BANCO_DADOS="root"
export PASSWORD_BANCO="root"
export EMAIL="${EMAIL:-seu-email@gmail.com}"
export SENHA="${SENHA:-sua-senha-app}"
export SECRET="${SECRET:-seu-jwt-secret-super-longo}"
export TIMEOUT_LIMIT="3600000"
echo -e "${GREEN}✅ Variáveis configuradas${NC}"
echo ""

# Limpar e compilar
echo -e "${BLUE}Step 4: Compilando projeto...${NC}"
cd /home/rodrigo/IdeaProjects/projetoFinalCatalisa
./mvnw clean compile -q
echo -e "${GREEN}✅ Compilação concluída${NC}"
echo ""

# Executar
echo -e "${BLUE}Step 5: Iniciando aplicação...${NC}"
echo ""
echo -e "${YELLOW}🔗 Endpoints:${NC}"
echo "   • API: http://localhost:8080"
echo "   • Swagger: http://localhost:8080/swagger-ui.html"
echo "   • Health: http://localhost:8080/actuator/health"
echo ""
echo -e "${YELLOW}💡 Dica: Abra outro terminal para testar:${NC}"
echo "   curl -s http://localhost:8080/api/usuarios | jq"
echo ""
echo -e "${YELLOW}Para parar: Pressione Ctrl+C${NC}"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Executar Spring Boot
./mvnw spring-boot:run

