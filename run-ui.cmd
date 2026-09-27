@echo off
REM File: run-ui.cmd
REM Purpose: Starts the studio under its older name, by passing every argument to run-product.cmd
REM Audience: A person or a script that still calls run-ui.cmd
REM Update when: run-product.cmd is renamed, or run-ui.cmd is retired

call "%~dp0run-product.cmd" %*
