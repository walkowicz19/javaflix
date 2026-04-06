# Guia de Deploy e Configuração - JavaFlix

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Pré-requisitos](#-pré-requisitos)
- [Configuração do Ambiente](#-configuração-do-ambiente)
- [Deploy Local](#-deploy-local)
- [Deploy em Produção](#-deploy-em-produção)
- [Configurações Avançadas](#-configurações-avançadas)
- [Monitoramento](#-monitoramento)
- [Backup e Recuperação](#-backup-e-recuperação)
- [Troubleshooting](#-troubleshooting)

---

## 🎯 Visão Geral

Este guia cobre todos os aspectos de deploy e configuração do JavaFlix, desde ambiente de desenvolvimento até produção.

### Componentes do Sistema

```
┌─────────────────────────────────────┐
│         Frontend (React)            │
│         Port: 5173 (dev)            │
│         Port: 80/443 (prod)         │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│       Backend (Quarkus)             │
│         Port: 8080                  │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│       PocketBase                    │
│         Port: 8090                  │
└─────────────────────────────────────┘
```

---

## 📦 Pré-requisitos

### Software Necessário

| Software | Versão Mínima | Recomendada | Download |
|----------|---------------|-------------|----------|
| **Java JDK** | 17 | 21 | [Oracle](https://www.oracle.com/java/technologies/downloads/) |
| **Node.js** | 18 | 20 LTS | [nodejs.org](https://nodejs.org/) |
| **Maven** | 3.8 | 3.9 | Incluído (wrapper) |
| **Git** | 2.30 | Latest | [git-scm.com](https://git-scm.com/) |
| **PocketBase** | 0.22 | Latest | [pocketbase.io](https://pocketbase.io/) |

### Verificar Instalações

```bash
# Java
java -version
# Deve mostrar: java version "17" ou superior

# Node.js
node --version
# Deve mostrar: v18.x.x ou superior

# npm
npm --version
# Deve mostrar: 9.x.x ou superior

# Git
git --version
# Deve mostrar: git version 2.x.x
```

### Hardware Recomendado

#### Desenvolvimento
- **CPU:** 2+ cores
- **RAM:** 4GB mínimo, 8GB recomendado
- **Disco:** 10GB livres
- **Internet:** 5 Mbps

#### Produção
- **CPU:** 4+ cores
- **RAM:** 8GB mínimo, 16GB recomendado
- **Disco:** 50GB+ (SSD recomendado)
- **Internet:** 100 Mbps

---

## ⚙️ Configuração do Ambiente

### 1. Clonar o Repositório

```bash
# Clone o projeto
git clone https://github.com/seu-usuario/javaflix.git
cd javaflix
```

### 2. Configurar PocketBase

#### Windows (PowerShell)

```powershell
# Baixar PocketBase
$version = "0.22.0"
$url = "https://github.com/pocketbase/pocketbase/releases/download/v$version/pocketbase_${version}_windows_amd64.zip"
Invoke-WebRequest -Uri $url -OutFile "pocketbase.zip"

# Extrair
Expand-Archive -Path "pocketbase.zip" -DestinationPath "." -Force

# Iniciar
.\pocketbase.exe serve --http="127.0.0.1:8090"
```

#### Linux/Mac

```bash
# Baixar PocketBase
wget https://github.com/pocketbase/pocketbase/releases/download/v0.22.0/pocketbase_0.22.0_linux_amd64.zip

# Extrair
unzip pocketbase_0.22.0_linux_amd64.zip

# Dar permissão de execução
chmod +x pocketbase

# Iniciar
./pocketbase serve --http="127.0.0.1:8090"
```

#### Configurar Admin

1. Acesse: http://127.0.0.1:8090/_/
2. Crie conta admin:
   - Email: admin@javaflix.com
   - Senha: (escolha uma senha forte)

#### Criar Collections

Execute o script de setup:

```bash
# Windows
.\setup-pocketbase.ps1

# Linux/Mac
./setup-pocketbase.sh
```

Ou crie manualmente:

**Collection: conteudos**
```json
{
  "name": "conteudos",
  "type": "base",
  "schema": [
    {"name": "titulo", "type": "text", "required": true},
    {"name": "genero", "type": "text", "required": true},
    {"name": "classificacaoEtaria", "type": "number", "required": true},
    {"name": "tipo", "type": "text", "required": true},
    {"name": "videoUrl", "type": "url"},
    {"name": "thumbnailUrl", "type": "url"},
    {"name": "descricao", "type": "text"},
    {"name": "duracaoMinutos", "type": "number"},
    {"name": "diretor", "type": "text"},
    {"name": "temporadas", "type": "number"},
    {"name": "episodiosPorTemporada", "type": "number"}
  ]
}
```

**Collection: users**
```json
{
  "name": "users",
  "type": "auth",
  "schema": [
    {"name": "name", "type": "text"},
    {"name": "avatar", "type": "file"}
  ]
}
```

**Collection: avaliacoes**
```json
{
  "name": "avaliacoes",
  "type": "base",
  "schema": [
    {"name": "conteudoId", "type": "relation", "required": true},
    {"name": "userId", "type": "relation", "required": true},
    {"name": "nota", "type": "number", "required": true, "min": 1, "max": 5},
    {"name": "comentario", "type": "text"}
  ]
}
```

### 3. Configurar Backend

#### application.properties

```properties
# Arquivo: src/main/resources/application.properties

# PocketBase Configuration
pocketbase.url=http://127.0.0.1:8090
pocketbase.admin.email=admin@javaflix.com
pocketbase.admin.password=sua-senha-aqui

# Quarkus Configuration
quarkus.http.port=8080
quarkus.http.cors=true
quarkus.http.cors.origins=http://localhost:5173
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS
quarkus.http.cors.headers=accept,authorization,content-type,x-requested-with

# Logging
quarkus.log.level=INFO
quarkus.log.category."br.com.javaflix".level=DEBUG

# REST Client
quarkus.rest-client."br.com.javaflix.client.PocketBaseClient".url=${pocketbase.url}
quarkus.rest-client."br.com.javaflix.client.PocketBaseClient".scope=javax.inject.Singleton
```

#### Variáveis de Ambiente (Opcional)

```bash
# Linux/Mac
export POCKETBASE_URL=http://127.0.0.1:8090
export POCKETBASE_ADMIN_EMAIL=admin@javaflix.com
export POCKETBASE_ADMIN_PASSWORD=sua-senha

# Windows (PowerShell)
$env:POCKETBASE_URL="http://127.0.0.1:8090"
$env:POCKETBASE_ADMIN_EMAIL="admin@javaflix.com"
$env:POCKETBASE_ADMIN_PASSWORD="sua-senha"
```

### 4. Configurar Frontend

#### .env (Desenvolvimento)

```bash
# Arquivo: frontend/.env

VITE_API_URL=http://localhost:8080/api
VITE_POCKETBASE_URL=http://127.0.0.1:8090
```

#### .env.production

```bash
# Arquivo: frontend/.env.production

VITE_API_URL=https://api.javaflix.com/api
VITE_POCKETBASE_URL=https://db.javaflix.com
```

---

## 🚀 Deploy Local

### Modo Desenvolvimento

#### Opção 1: Scripts Automatizados

**Windows:**
```powershell
# Iniciar tudo
.\start-dev.ps1
```

**Linux/Mac:**
```bash
# Iniciar tudo
./start-dev.sh
```

#### Opção 2: Manual (3 Terminais)

**Terminal 1: PocketBase**
```bash
./pocketbase serve --http="127.0.0.1:8090"
```

**Terminal 2: Backend**
```bash
cd javaflix
./mvnw quarkus:dev
```

**Terminal 3: Frontend**
```bash
cd frontend
npm run dev
```

### Verificar Instalação

1. **PocketBase:** http://127.0.0.1:8090/_/
2. **Backend:** http://localhost:8080/q/health
3. **Frontend:** http://localhost:5173
4. **Swagger:** http://localhost:8080/q/swagger-ui

### Inserir Dados de Teste

```bash
# Windows
.\inserir-dados-teste.ps1

# Linux/Mac
./inserir-dados-teste.sh
```

---

## 🌐 Deploy em Produção

### 1. Build do Backend

#### JAR Padrão

```bash
./mvnw clean package

# Executar
java -jar target/quarkus-app/quarkus-run.jar
```

#### Uber-JAR

```bash
./mvnw clean package -Dquarkus.package.jar.type=uber-jar

# Executar
java -jar target/*-runner.jar
```

#### Nativo (GraalVM)

```bash
# Com GraalVM instalado
./mvnw clean package -Dnative

# Ou com Docker
./mvnw clean package -Dnative -Dquarkus.native.container-build=true

# Executar
./target/streaming-api-1.0.0-SNAPSHOT-runner
```

### 2. Build do Frontend

```bash
cd frontend

# Instalar dependências
npm ci

# Build para produção
npm run build

# Arquivos gerados em: dist/
```

### 3. Deploy com Docker

#### Dockerfile - Backend

```dockerfile
# Arquivo: Dockerfile.backend

FROM registry.access.redhat.com/ubi8/openjdk-17:1.18

ENV LANGUAGE='en_US:en'

COPY --chown=185 target/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/app/ /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
```

#### Dockerfile - Frontend

```dockerfile
# Arquivo: frontend/Dockerfile

# Build stage
FROM node:20-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### docker-compose.yml

```yaml
version: '3.8'

services:
  pocketbase:
    image: ghcr.io/muchobien/pocketbase:latest
    container_name: javaflix-pocketbase
    restart: unless-stopped
    ports:
      - "8090:8090"
    volumes:
      - ./pb_data:/pb_data
    healthcheck:
      test: wget --no-verbose --tries=1 --spider http://localhost:8090/api/health || exit 1
      interval: 5s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: .
      dockerfile: Dockerfile.backend
    container_name: javaflix-backend
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      - POCKETBASE_URL=http://pocketbase:8090
      - QUARKUS_HTTP_CORS_ORIGINS=http://localhost:3000
    depends_on:
      pocketbase:
        condition: service_healthy

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: javaflix-frontend
    restart: unless-stopped
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  pb_data:
```

#### Executar com Docker

```bash
# Build e iniciar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

### 4. Deploy em Cloud

#### AWS (Elastic Beanstalk)

```bash
# Instalar EB CLI
pip install awsebcli

# Inicializar
eb init -p docker javaflix

# Criar ambiente
eb create javaflix-prod

# Deploy
eb deploy

# Abrir no navegador
eb open
```

#### Heroku

```bash
# Login
heroku login

# Criar app
heroku create javaflix-app

# Deploy
git push heroku main

# Abrir
heroku open
```

#### Google Cloud Run

```bash
# Build
gcloud builds submit --tag gcr.io/PROJECT-ID/javaflix

# Deploy
gcloud run deploy javaflix \
  --image gcr.io/PROJECT-ID/javaflix \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

---

## 🔧 Configurações Avançadas

### SSL/TLS (HTTPS)

#### Nginx com Let's Encrypt

```nginx
# /etc/nginx/sites-available/javaflix

server {
    listen 80;
    server_name javaflix.com www.javaflix.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name javaflix.com www.javaflix.com;

    ssl_certificate /etc/letsencrypt/live/javaflix.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/javaflix.com/privkey.pem;

    # Frontend
    location / {
        root /var/www/javaflix/frontend;
        try_files $uri $uri/ /index.html;
    }

    # Backend API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # PocketBase
    location /pb {
        proxy_pass http://localhost:8090;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Load Balancing

```nginx
upstream backend {
    least_conn;
    server backend1:8080 weight=3;
    server backend2:8080 weight=2;
    server backend3:8080 weight=1;
}

server {
    location /api {
        proxy_pass http://backend;
    }
}
```

### Caching

```nginx
# Cache de assets estáticos
location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}

# Cache de API (opcional)
location /api/conteudos {
    proxy_cache api_cache;
    proxy_cache_valid 200 10m;
    proxy_pass http://localhost:8080;
}
```

---

## 📊 Monitoramento

### Health Checks

#### Backend

```bash
# Health endpoint
curl http://localhost:8080/q/health

# Liveness
curl http://localhost:8080/q/health/live

# Readiness
curl http://localhost:8080/q/health/ready
```

#### Métricas (Prometheus)

```properties
# application.properties
quarkus.micrometer.enabled=true
quarkus.micrometer.export.prometheus.enabled=true
```

Acesse: http://localhost:8080/q/metrics

### Logs

#### Configurar Logging

```properties
# application.properties

# Nível de log
quarkus.log.level=INFO
quarkus.log.category."br.com.javaflix".level=DEBUG

# Formato
quarkus.log.console.format=%d{HH:mm:ss} %-5p [%c{2.}] (%t) %s%e%n

# Arquivo de log
quarkus.log.file.enable=true
quarkus.log.file.path=/var/log/javaflix/app.log
quarkus.log.file.rotation.max-file-size=10M
quarkus.log.file.rotation.max-backup-index=5
```

#### Visualizar Logs

```bash
# Docker
docker-compose logs -f backend

# Arquivo
tail -f /var/log/javaflix/app.log

# Filtrar erros
grep ERROR /var/log/javaflix/app.log
```

---

## 💾 Backup e Recuperação

### Backup do PocketBase

```bash
# Backup manual
cp -r pb_data pb_data_backup_$(date +%Y%m%d)

# Backup automático (cron)
0 2 * * * /usr/local/bin/backup-pocketbase.sh
```

#### Script de Backup

```bash
#!/bin/bash
# backup-pocketbase.sh

BACKUP_DIR="/backups/pocketbase"
DATE=$(date +%Y%m%d_%H%M%S)

# Criar backup
tar -czf "$BACKUP_DIR/pb_data_$DATE.tar.gz" pb_data/

# Manter apenas últimos 7 dias
find "$BACKUP_DIR" -name "pb_data_*.tar.gz" -mtime +7 -delete
```

### Restaurar Backup

```bash
# Parar PocketBase
docker-compose stop pocketbase

# Restaurar
tar -xzf pb_data_backup.tar.gz

# Reiniciar
docker-compose start pocketbase
```

---

## 🔍 Troubleshooting

### Problemas Comuns

#### Backend não inicia

**Erro:** `Port 8080 already in use`

**Solução:**
```bash
# Encontrar processo
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac

# Matar processo
taskkill /PID <PID> /F        # Windows
kill -9 <PID>                 # Linux/Mac
```

#### PocketBase não conecta

**Erro:** `Connection refused`

**Solução:**
1. Verificar se PocketBase está rodando
2. Verificar URL em application.properties
3. Verificar firewall

#### Frontend não carrega API

**Erro:** `CORS policy`

**Solução:**
```properties
# application.properties
quarkus.http.cors=true
quarkus.http.cors.origins=http://localhost:5173
```

### Logs de Debug

```bash
# Backend
./mvnw quarkus:dev -Dquarkus.log.level=DEBUG

# Frontend
npm run dev -- --debug

# PocketBase
./pocketbase serve --debug
```

---

## 📞 Suporte

### Recursos

- **Documentação:** [/docs](.)
- **Issues:** [GitHub Issues](https://github.com/seu-usuario/javaflix/issues)
- **Wiki:** [GitHub Wiki](https://github.com/seu-usuario/javaflix/wiki)

---

**Última atualização:** 2026-04-05  
**Versão:** 1.0  
**Status:** ✅ Completo