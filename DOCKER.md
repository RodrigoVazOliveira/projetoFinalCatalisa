# Docker e Podman - Guia de Uso

## Arquivos Criados

- **Dockerfile** - Multi-stage build otimizado para a aplicação
- **docker-compose.yml** - Orquestração da aplicação com MySQL
- **.dockerignore** - Arquivos ignorados no build
- **.env.example** - Template de variáveis de ambiente

## Pré-requisitos

### Docker
```bash
# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
```

### Podman
```bash
# Instalar Podman (Ubuntu/Debian)
sudo apt-get install podman

# Instalar Docker Compose para Podman
sudo apt-get install podman-docker
```

## Como Usar

### 1. Com Docker Compose (Recomendado)

```bash
# Clonar variáveis de ambiente
cp .env.example .env

# Editar .env com suas credenciais (Gmail, JWT Secret, etc)
nano .env

# Iniciar aplicação e banco de dados
docker-compose up -d

# Verificar logs
docker-compose logs -f app

# Parar aplicação
docker-compose down

# Limpar volumes (banco de dados)
docker-compose down -v
```

### 2. Com Podman Compose

```bash
# Alternativa com Podman
podman-compose up -d

# Verificar containers
podman ps

# Ver logs
podman logs -f zupayments-app
```

### 3. Build Manual

```bash
# Build da imagem
docker build -t zupayments:1.0.0 .

# Executar container
docker run -d \
  --name zupayments \
  -p 8080:8080 \
  -e URL=jdbc:mysql://mysql-host:3306/zupayments \
  -e USER_BANCO_DADOS=root \
  -e PASSWORD_BANCO=root \
  -e EMAIL=seu-email@gmail.com \
  -e SENHA=sua-senha \
  -e SECRET=seu-secret-jwt \
  -e TIMEOUT_LIMIT=3600000 \
  zupayments:1.0.0
```

## Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `URL` | URL de conexão MySQL | `jdbc:mysql://mysql:3306/zupayments` |
| `USER_BANCO_DADOS` | Usuário do banco | `root` |
| `PASSWORD_BANCO` | Senha do banco | `root` |
| `EMAIL` | Email para SMTP (Gmail) | Requerido |
| `SENHA` | Senha do app Gmail | Requerido |
| `SECRET` | Secret JWT | Requerido (mín 32 caracteres) |
| `TIMEOUT_LIMIT` | Timeout JWT em ms | `3600000` (1 hora) |

## Acessar a Aplicação

- **URL da Aplicação**: http://localhost:8080
- **Swagger/OpenAPI**: http://localhost:8080/swagger-ui.html
- **MySQL**: localhost:3306

## Credenciais Padrão (Docker Compose)

- **Banco de Dados**: zupayments
- **Usuário MySQL**: root
- **Senha MySQL**: root

## Troubleshooting

### Erro: "Cannot connect to MySQL"

```bash
# Verificar se MySQL está saudável
docker-compose ps

# Verificar logs do MySQL
docker-compose logs mysql

# Reiniciar containers
docker-compose restart
```

### Erro: "Permission denied while trying to connect to Docker daemon"

```bash
# Adicionar usuário ao grupo docker
sudo usermod -aG docker $USER
newgrp docker
```

### Limpar tudo e começar do zero

```bash
# Parar containers
docker-compose down

# Remover volumes
docker-compose down -v

# Remover imagens
docker rmi zupayments:1.0.0

# Iniciar novamente
docker-compose up -d
```

## Performance e Otimizações

### Dockerfile Multi-Stage
- Reduz tamanho final da imagem
- Stage de build é descartado
- Apenas runtime necessário na imagem final

### Health Check
- Monitora saúde da aplicação
- Reinicia automaticamente se necessário
- Endpoint: `/actuator/health`

### MySQL Alpine
- Usa imagem Alpine (mais leve)
- Mais rápido para download e build

## Segurança

⚠️ **Importante**: Não versione o arquivo `.env` em produção

```bash
# Adicionar ao .gitignore
echo ".env" >> .gitignore
```

Use secrets do Docker Swarm ou Kubernetes em produção:

```bash
# Docker Swarm
docker secret create jwt_secret -
docker secret create email_secret -
```

## Próximas Etapas

1. Configure as credenciais no arquivo `.env`
2. Execute `docker-compose up -d`
3. Teste a aplicação em http://localhost:8080
4. Acesse o Swagger para testar os endpoints

