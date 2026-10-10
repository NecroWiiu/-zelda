@echo off
setlocal EnableDelayedExpansion
chcp 65001 >nul
cd /d "%~dp0"
title HyruleCraft - compilar e instalar

echo ==============================================
echo   HyruleCraft: Ocarina Edition  (Fabric 1.20.1)
echo ==============================================
echo.

set "TOOLS=%~dp0.tools"
if not exist "%TOOLS%" mkdir "%TOOLS%"

rem ---------- 1) Java 17 ----------
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" goto :javaok
if exist "%TOOLS%\jdk17\bin\java.exe" ( set "JAVA_HOME=%TOOLS%\jdk17" & goto :javaok )
echo [1/4] Descargando Java 17 (solo la primera vez, ~190 MB)...
powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest 'https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse' -OutFile '%TOOLS%\jdk17.zip'"
if errorlevel 1 goto :fail
powershell -NoProfile -Command "Expand-Archive -Force '%TOOLS%\jdk17.zip' '%TOOLS%\jdk_tmp'"
for /d %%d in ("%TOOLS%\jdk_tmp\*") do move "%%d" "%TOOLS%\jdk17" >nul
rmdir /s /q "%TOOLS%\jdk_tmp" 2>nul
del "%TOOLS%\jdk17.zip" 2>nul
set "JAVA_HOME=%TOOLS%\jdk17"
:javaok
echo [1/4] Java: %JAVA_HOME%

rem ---------- 2) Gradle ----------
if exist "%TOOLS%\gradle-8.7\bin\gradle.bat" goto :gradleok
echo [2/4] Descargando Gradle 8.7 (solo la primera vez)...
powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest 'https://services.gradle.org/distributions/gradle-8.7-bin.zip' -OutFile '%TOOLS%\gradle.zip'"
if errorlevel 1 goto :fail
powershell -NoProfile -Command "Expand-Archive -Force '%TOOLS%\gradle.zip' '%TOOLS%'"
del "%TOOLS%\gradle.zip" 2>nul
:gradleok
echo [2/4] Gradle listo

rem ---------- 3) Compilar ----------
echo [3/4] Compilando (la primera vez baja Minecraft y tarda unos minutos)...
call "%TOOLS%\gradle-8.7\bin\gradle.bat" build --no-daemon
if errorlevel 1 goto :fail

if not exist dist mkdir dist
copy /y "build\libs\hyrulecraft-1.0.0.jar" "dist\hyrulecraft-1.0.0.jar" >nul
echo.
echo [3/4] Listo: dist\hyrulecraft-1.0.0.jar

rem ---------- 4) Instalar en PrismLauncher ----------
set "INST=%APPDATA%\PrismLauncher\instances"
if not exist "%INST%" (
  echo [4/4] No encontre PrismLauncher. Copia dist\hyrulecraft-1.0.0.jar a la carpeta mods de tu instancia.
  goto :end
)
echo.
echo [4/4] Instancias de PrismLauncher:
set /a n=0
for /d %%i in ("%INST%\*") do (
  if exist "%%i\instance.cfg" ( set /a n+=1 & set "I!n!=%%i" & echo   !n!^) %%~nxi )
)
echo   0^) No instalar
set /p CH=Elige el numero de la instancia (1.20.1 Fabric): 
if "%CH%"=="0" goto :end
set "SEL=!I%CH%!"
if not defined SEL goto :end
if exist "%SEL%\minecraft" ( set "MODS=%SEL%\minecraft\mods" ) else ( set "MODS=%SEL%\.minecraft\mods" )
if not exist "%MODS%" mkdir "%MODS%"
copy /y "dist\hyrulecraft-1.0.0.jar" "%MODS%\" >nul
echo Instalado en %MODS%
echo Recuerda: necesitas Fabric API 0.92.2+1.20.1 en esa instancia.
goto :end

:fail
echo.
echo *** Algo fallo. Copia el mensaje de error de arriba y mandamelo para arreglarlo. ***
:end
echo.
pause
