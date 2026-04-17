# GitHub Actions - CI/CD Pipeline

Este projeto utiliza GitHub Actions para automatizar o build, testes e análise de qualidade de código.

## 📋 Workflows Configurados

### 1. **CI/CD Pipeline** (`.github/workflows/ci-cd.yml`)

Executado automaticamente em:
- **Push** para `main` ou `develop`
- **Pull Requests** para `main` ou `develop`

#### Passos:
1. ✅ Checkout do código
2. ✅ Setup do JDK 17
3. ✅ Compilação do projeto (Maven clean compile)
4. ✅ Execução de testes unitários (Maven test)
5. ✅ Build do projeto (Maven package)
6. ✅ Upload de artefatos (JAR files)
7. ✅ Upload de relatórios de teste
8. ✅ Publicação de resultados dos testes
9. ✅ Comentários automáticos em PRs

#### Artefatos Gerados:
- `build-artifacts/` - Arquivos JAR (mantidos por 7 dias)
- `test-reports/` - Relatórios de teste (mantidos por 30 dias)

---

### 2. **Análise de Qualidade de Código** (`.github/workflows/quality-analysis.yml`)

Executado automaticamente em:
- **Push** para `main` ou `develop`
- **Pull Requests** para `main` ou `develop`

#### Passos:
1. ✅ Checkout do código
2. ✅ Setup do JDK 17
3. ✅ Execução de testes com cobertura JaCoCo
4. ✅ Upload de cobertura para Codecov
5. ✅ Análise SonarQube (se configurado)

#### Requisitos para SonarQube:
Para ativar a análise SonarQube, adicione os seguintes secrets no GitHub:
- `SONAR_TOKEN` - Token de autenticação SonarQube
- `SONAR_HOST_URL` - URL da instância SonarQube (ex: https://sonarqube.example.com)

---

## 🔧 Configuração de Secrets (Opcional)

Para habilitar funcionalidades adicionais, adicione os seguintes secrets no repositório:

1. Acesse: **Settings** → **Secrets and variables** → **Actions**
2. Clique em **New repository secret**
3. Adicione:

```
Name: SONAR_TOKEN
Secret: <seu-token-sonarqube>

Name: SONAR_HOST_URL
Secret: <sua-url-sonarqube>
```

---

## 📊 Status e Badges

Adicione o badge de status ao seu README principal:

```markdown
[![CI/CD Pipeline](https://github.com/seu-usuario/projetoFinalCatalisa/actions/workflows/ci-cd.yml/badge.svg?branch=main)](https://github.com/seu-usuario/projetoFinalCatalisa/actions/workflows/ci-cd.yml)

[![Análise de Qualidade](https://github.com/seu-usuario/projetoFinalCatalisa/actions/workflows/quality-analysis.yml/badge.svg?branch=main)](https://github.com/seu-usuario/projetoFinalCatalisa/actions/workflows/quality-analysis.yml)
```

---

## 🚀 Como Usar

### Verificar Status do Workflow:
1. Vá para **Actions** no repositório GitHub
2. Clique no workflow que deseja acompanhar
3. Veja os logs em tempo real

### Baixar Artefatos:
1. Vá para **Actions** → Clique na execução específica
2. Desça até **Artifacts**
3. Clique no artefato desejado para fazer download

### Visualizar Resultados de Testes:
- Os resultados aparecem automaticamente em **Checks** no PR
- Um comentário é adicionado automaticamente ao PR com o status

---

## 📝 Variáveis de Ambiente

O workflow utiliza a seguinte variável para otimizar o build:
```yaml
MAVEN_OPTS: -Xmx1024m  # Aloca 1GB de memória para Maven
```

---

## ⚙️ Personalização

Para modificar os workflows:

1. Edite os arquivos em `.github/workflows/`
2. Faça commit e push
3. O workflow será automaticamente atualizado na próxima execução

### Triggers Disponíveis:
- `push` - Em commits push
- `pull_request` - Em pull requests
- `schedule` - Em horários agendados (cron jobs)
- `manual` - Disparo manual (workflow_dispatch)

---

## 🐛 Troubleshooting

### Workflow falhando no build?
1. Verifique os logs em **Actions**
2. Confirme que o pom.xml está correto
3. Teste localmente com `mvn clean install`

### Testes falhando no CI?
1. Verifique se os testes passam localmente
2. Procure por diferenças de ambiente (versão Java, SO)
3. Adicione logs ao workflow para debug

### SonarQube não executando?
1. Verifique se os secrets foram configurados corretamente
2. Confirme que o token está válido
3. Verifique a conectividade com o servidor SonarQube

---

## 📚 Referências

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven Plugin for GitHub Actions](https://github.com/actions/setup-java)
- [SonarQube Scanner for Maven](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/)
- [Codecov GitHub Action](https://github.com/codecov/codecov-action)

---

**Última atualização:** 2026-04-17

