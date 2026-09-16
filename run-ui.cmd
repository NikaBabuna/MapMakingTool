@echo off
REM Launch the skeleton Swing UI (CMD-friendly).
REM Installs engine+ui into the local Maven repo, then runs the ui module only.

cd /d "%~dp0"
call mvnw.cmd -pl ui -am install -DskipTests
if errorlevel 1 exit /b 1
call mvnw.cmd -pl ui exec:java
