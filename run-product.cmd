@echo off
REM Primary Aethelgard launch (F-026): MapHost + Next + Tauri desktop shell.
REM Requires: JDK 21, Node.js, Rust (for first Tauri build).

cd /d "%~dp0"

echo [1/3] Installing Java modules...
call mvnw.cmd -pl ui -am install -DskipTests
if errorlevel 1 exit /b 1

echo [2/3] Starting Next.js (ui/web) on http://localhost:3000 ...
start "Aethelgard Next" cmd /c "cd /d "%~dp0ui\web" && if not exist node_modules npm install && npm run dev"

echo [3/3] Starting Tauri desktop (spawns MapHost, webview -> :3000)...
cd /d "%~dp0ui\desktop"
if not exist node_modules call npm install
if errorlevel 1 exit /b 1
call npm run dev
