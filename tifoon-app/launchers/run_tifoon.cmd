@echo off
cd /d "%~dp0"
for /F "tokens=*" %%i in ('dir /b tifoon-app-*.jar') do java -jar %%i
