#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONTEND_DIR="${ROOT_DIR}/frontend"

TARGET="${1:-all}"

build_backend() {
  echo "==> Building backend (Maven)"
  cd "${ROOT_DIR}"
  ./mvnw -q -DskipTests clean package
  echo "==> Backend package done: ${ROOT_DIR}/target"
}

build_frontend() {
  echo "==> Building frontend (Vite)"
  cd "${FRONTEND_DIR}"

  if [ -f "package-lock.json" ]; then
    if ! npm ci; then
      echo "npm ci failed, retrying with npm install..."
      npm install
    fi
  else
    npm install
  fi

  npm run build
  echo "==> Frontend package done: ${FRONTEND_DIR}/dist"
}

case "${TARGET}" in
  backend)
    build_backend
    ;;
  frontend)
    build_frontend
    ;;
  all)
    build_backend
    build_frontend
    ;;
  *)
    echo "Usage: ./scripts/package.sh [backend|frontend|all]"
    exit 1
    ;;
esac

