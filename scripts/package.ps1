param(
  [ValidateSet("backend", "frontend", "all")]
  [string]$Target = "all"
)

$ErrorActionPreference = "Stop"
$RootDir = Split-Path -Parent $PSScriptRoot
$FrontendDir = Join-Path $RootDir "frontend"

function Invoke-Step {
  param(
    [string]$Description,
    [scriptblock]$Action
  )
  Write-Host "==> $Description"
  & $Action
  if ($LASTEXITCODE -ne 0) {
    throw "$Description failed with exit code $LASTEXITCODE"
  }
}

function Build-Backend {
  Set-Location $RootDir
  Invoke-Step "Building backend (Maven)" { & "$RootDir\mvnw.cmd" -q -DskipTests clean package }
  Write-Host "==> Backend package done: $RootDir\target"
}

function Build-Frontend {
  Set-Location $FrontendDir

  if (Test-Path "$FrontendDir\package-lock.json") {
    & npm ci
    if ($LASTEXITCODE -ne 0) {
      Write-Warning "npm ci failed, retrying with npm install..."
      & npm install
      if ($LASTEXITCODE -ne 0) {
        throw "Frontend dependency install failed. Please close dev server or antivirus lock, then retry."
      }
    }
  } else {
    Invoke-Step "Installing frontend dependencies (npm install)" { & npm install }
  }

  Invoke-Step "Building frontend (Vite)" { & npm run build }
  Write-Host "==> Frontend package done: $FrontendDir\dist"
}

switch ($Target) {
  "backend" { Build-Backend }
  "frontend" { Build-Frontend }
  "all" {
    Build-Backend
    Build-Frontend
  }
}

