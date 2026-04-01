FROM node:20-bookworm-slim AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci --no-audit --no-fund

COPY . ./

# 分离部署时可在平台环境变量中传入后端地址，例如：
# VITE_API_BASE=https://api.your-domain.com/api
ARG VITE_API_BASE=
ENV VITE_API_BASE=${VITE_API_BASE}

RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80
