# ──────────────────────────────────────────────────────────
# Stage 1: Dependências (camada cacheável separada do código)
# ──────────────────────────────────────────────────────────
FROM node:22-alpine AS deps
WORKDIR /app
# Copia APENAS manifests — só invalida esta camada se mudar dependências
COPY package.json package-lock.json ./
# --prefer-offline usa cache npm local quando disponível; --no-audit/fund elimina requests extras
RUN npm ci --prefer-offline --no-audit --no-fund

# ──────────────────────────────────────────────────────────
# Stage 2: Build (re-usa node_modules cacheados)
# ──────────────────────────────────────────────────────────
FROM deps AS build
COPY . .
RUN npm run build

# ──────────────────────────────────────────────────────────
# Stage 3: Runtime Nginx mínimo
# ──────────────────────────────────────────────────────────
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
