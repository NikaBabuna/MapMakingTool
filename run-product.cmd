@echo off
REM Primary Aethelgard launch (F-026): MapHost + Next + Tauri desktop shell.
REM Tauri starts Next (beforeDevCommand) and waits for http://localhost:3000.
REM Requires: JDK 21, Node.js, Rust (for first Tauri build).

cd /d "%~dp0"

echo [1/4] Installing Java modules...
call mvnw.cmd -pl ui -am install -DskipTests
if errorlevel 1 exit /b 1

echo [2/4] Ensuring Next deps (ui/web)...
pushd "%~dp0ui\web"
if not exist node_modules call npm install
if errorlevel 1 popd & exit /b 1
popd

echo [3/4] Ensuring Tauri deps (ui/desktop)...
pushd "%~dp0ui\desktop"
if not exist node_modules call npm install
if errorlevel 1 popd & exit /b 1

echo [4/4] Starting Tauri (starts Next, spawns MapHost, opens window)...
call npm run dev
set EXITCODE=%ERRORLEVEL%
popd
exit /b %EXITCODE%
