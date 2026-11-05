@echo off
setlocal
cd /d "%~dp0"
title SmartStock - Iniciando

if not exist ".env" (
  echo No se encontro el archivo .env en esta carpeta.
  pause
  exit /b 1
)
for /f "usebackq eol=# tokens=1,* delims==" %%a in (".env") do set "%%a=%%b"
set "SPRING_PROFILES_ACTIVE=supabase"

rem --- Backend: usa el .jar; si no existe, lo compila una sola vez ---
if not exist "backend\target\smartstock-backend.jar" (
  echo Compilando el backend por primera vez, un momento...
  pushd backend
  call mvn -q -DskipTests package
  popd
)
if not exist "backend\target\smartstock-backend.jar" (
  echo No se pudo crear backend\target\smartstock-backend.jar
  pause
  exit /b 1
)

echo Iniciando servicios...
start "SmartStock-Prediccion" /min /d "%~dp0prediccion" cmd /c ".venv\Scripts\python.exe -m uvicorn app.main:app --port 8000"
start "SmartStock-Backend" /min /d "%~dp0backend" cmd /c "java -jar target\smartstock-backend.jar"

echo Esperando a que el sistema este listo (hasta 2 minutos)...
set /a intentos=0
:espera
set /a intentos+=1
if %intentos% gtr 60 (
  echo El backend no respondio. Revisa la ventana minimizada "SmartStock-Backend".
  taskkill /FI "WINDOWTITLE eq SmartStock-*" /T /F >nul 2>&1
  pause
  exit /b 1
)
timeout /t 2 /nobreak >nul
curl -s -o nul -f http://localhost:8080/api/health
if errorlevel 1 goto espera

echo Abriendo SmartStock...
if exist "desktop\target\dist\SmartStock\SmartStock.exe" (
  start /wait "" "desktop\target\dist\SmartStock\SmartStock.exe"
) else (
  pushd desktop
  call mvn javafx:run
  popd
)

echo Cerrando servicios...
taskkill /FI "WINDOWTITLE eq SmartStock-*" /T /F >nul 2>&1
exit /b 0
