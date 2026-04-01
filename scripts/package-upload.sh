#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="${ROOT_DIR}/dist-upload"
STAGE_DIR="${OUT_DIR}/_stage"
TARGET="${1:-all}"

reset_dir() {
  rm -rf "$1"
  mkdir -p "$1"
}

require_path() {
  local p="$1"
  if [ ! -e "$p" ]; then
    echo "Missing required path: $p" >&2
    exit 1
  fi
}

pack_backend() {
  local stage="${STAGE_DIR}/backend"
  reset_dir "$stage"

  # Keep minimal backend package, same as manual successful upload
  require_path "${ROOT_DIR}/Dockerfile"
  require_path "${ROOT_DIR}/pom.xml"
  require_path "${ROOT_DIR}/src"

  cp "${ROOT_DIR}/Dockerfile" "$stage/"
  cp "${ROOT_DIR}/pom.xml" "$stage/"
  cp -R "${ROOT_DIR}/src" "$stage/"

  (cd "$stage" && zip -qr "${OUT_DIR}/backend-upload.zip" .)
  echo "==> Generated ${OUT_DIR}/backend-upload.zip"
}

pack_frontend() {
  local stage="${STAGE_DIR}/frontend"
  reset_dir "$stage"

  require_path "${ROOT_DIR}/frontend/Dockerfile"
  require_path "${ROOT_DIR}/frontend/nginx.conf"
  require_path "${ROOT_DIR}/frontend/package.json"
  require_path "${ROOT_DIR}/frontend/vite.config.js"
  require_path "${ROOT_DIR}/frontend/index.html"
  require_path "${ROOT_DIR}/frontend/src"

  cp "${ROOT_DIR}/frontend/Dockerfile" "$stage/"
  cp "${ROOT_DIR}/frontend/nginx.conf" "$stage/"
  cp "${ROOT_DIR}/frontend/package.json" "$stage/"
  [ -f "${ROOT_DIR}/frontend/package-lock.json" ] && cp "${ROOT_DIR}/frontend/package-lock.json" "$stage/"
  cp "${ROOT_DIR}/frontend/vite.config.js" "$stage/"
  cp "${ROOT_DIR}/frontend/index.html" "$stage/"
  cp -R "${ROOT_DIR}/frontend/src" "$stage/"
  [ -d "${ROOT_DIR}/frontend/public" ] && cp -R "${ROOT_DIR}/frontend/public" "$stage/"
  [ -f "${ROOT_DIR}/frontend/.dockerignore" ] && cp "${ROOT_DIR}/frontend/.dockerignore" "$stage/"

  (cd "$stage" && zip -qr "${OUT_DIR}/frontend-upload.zip" .)
  echo "==> Generated ${OUT_DIR}/frontend-upload.zip"
}

mkdir -p "$OUT_DIR"
reset_dir "$STAGE_DIR"
rm -f "${OUT_DIR}/backend-upload.zip" "${OUT_DIR}/frontend-upload.zip"

case "$TARGET" in
  backend)
    pack_backend
    ;;
  frontend)
    pack_frontend
    ;;
  all)
    pack_backend
    pack_frontend
    ;;
  *)
    echo "Usage: ./scripts/package-upload.sh [backend|frontend|all]"
    exit 1
    ;;
esac

rm -rf "$STAGE_DIR"
echo "==> Upload packages ready in: ${OUT_DIR}"

