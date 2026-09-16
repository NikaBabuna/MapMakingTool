@echo off
REM Launch the product map window (CMD-friendly).
REM Installs engine+product into the local Maven repo, then runs the product module only.

cd /d "%~dp0"
call mvnw.cmd -pl product -am install -DskipTests
if errorlevel 1 exit /b 1
call mvnw.cmd -pl product exec:java
