param(
  [ValidateSet("backend", "frontend", "all")]
  [string]$Target = "all"
)

$ErrorActionPreference = "Stop"
$RootDir = Split-Path -Parent $PSScriptRoot
$OutDir = Join-Path $RootDir "dist-upload"
$StageDir = Join-Path $OutDir "_stage"

Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem

function Reset-Dir([string]$Path) {
  if (Test-Path $Path) {
    Remove-Item -Recurse -Force $Path
  }
  New-Item -ItemType Directory -Path $Path | Out-Null
}

function Add-ItemSafe([string]$Source, [string]$Dest) {
  if (-not (Test-Path $Source)) {
    throw "Missing required path: $Source"
  }
  Copy-Item -Recurse -Force $Source $Dest
}

function New-ZipNormalized([string]$SourceDir, [string]$ZipPath) {
  if (Test-Path $ZipPath) { Remove-Item -Force $ZipPath }
  $sourceRoot = (Resolve-Path $SourceDir).Path

  $zip = [System.IO.Compression.ZipFile]::Open($ZipPath, [System.IO.Compression.ZipArchiveMode]::Create)
  try {
    $files = Get-ChildItem -Recurse -File $sourceRoot
    foreach ($file in $files) {
      $relative = $file.FullName.Substring($sourceRoot.Length).TrimStart('\','/')
      $entryName = $relative -replace '\\','/'
      [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile(
        $zip,
        $file.FullName,
        $entryName,
        [System.IO.Compression.CompressionLevel]::Optimal
      ) | Out-Null
    }
  } finally {
    $zip.Dispose()
  }
}

function Pack-Backend {
  $backendStage = Join-Path $StageDir "backend"
  Reset-Dir $backendStage

  Add-ItemSafe "$RootDir\Dockerfile" $backendStage
  Add-ItemSafe "$RootDir\pom.xml" $backendStage
  Add-ItemSafe "$RootDir\src" $backendStage
  if (Test-Path "$RootDir\.dockerignore") {
    Add-ItemSafe "$RootDir\.dockerignore" $backendStage
  }

  $zipPath = Join-Path $OutDir "backend-upload.zip"
  New-ZipNormalized -SourceDir $backendStage -ZipPath $zipPath
  Write-Host "==> Generated $zipPath"
}

function Pack-Frontend {
  $frontendStage = Join-Path $StageDir "frontend"
  Reset-Dir $frontendStage

  Add-ItemSafe "$RootDir\frontend\Dockerfile" $frontendStage
  Add-ItemSafe "$RootDir\frontend\nginx.conf" $frontendStage
  Add-ItemSafe "$RootDir\frontend\package.json" $frontendStage
  if (Test-Path "$RootDir\frontend\package-lock.json") {
    Add-ItemSafe "$RootDir\frontend\package-lock.json" $frontendStage
  }
  Add-ItemSafe "$RootDir\frontend\vite.config.js" $frontendStage
  Add-ItemSafe "$RootDir\frontend\index.html" $frontendStage
  Add-ItemSafe "$RootDir\frontend\src" $frontendStage
  if (Test-Path "$RootDir\frontend\public") {
    Add-ItemSafe "$RootDir\frontend\public" $frontendStage
  }
  if (Test-Path "$RootDir\frontend\.dockerignore") {
    Add-ItemSafe "$RootDir\frontend\.dockerignore" $frontendStage
  }

  $zipPath = Join-Path $OutDir "frontend-upload.zip"
  New-ZipNormalized -SourceDir $frontendStage -ZipPath $zipPath
  Write-Host "==> Generated $zipPath"
}

New-Item -ItemType Directory -Path $OutDir -Force | Out-Null
Reset-Dir $StageDir

switch ($Target) {
  "backend" { Pack-Backend }
  "frontend" { Pack-Frontend }
  "all" {
    Pack-Backend
    Pack-Frontend
  }
}

Remove-Item -Recurse -Force $StageDir
Write-Host "==> Upload packages ready in: $OutDir"

