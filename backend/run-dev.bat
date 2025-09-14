@echo off
chcp 65001 > nul

echo 🚀 ERP 시스템 백엔드 개발 서버를 시작합니다...
echo.

echo ☕ Java 버전 확인:
java -version
echo.

echo 📦 Maven 버전 확인:
call mvnw.cmd -version
echo.

echo 📊 데이터베이스를 선택하세요:
echo 1^) H2 데이터베이스 ^(개발용, 권장^)
echo 2^) PostgreSQL ^(Docker 필요^)
set /p db_choice="선택 (1 또는 2): "

if "%db_choice%"=="1" (
    echo.
    echo 🔧 H2 데이터베이스로 개발 서버를 시작합니다...
    echo 📍 H2 콘솔: http://localhost:8080/h2-console
    echo 📍 Swagger UI: http://localhost:8080/swagger-ui/index.html
    echo.
    call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
) else if "%db_choice%"=="2" (
    echo.
    echo 🐳 PostgreSQL Docker 컨테이너를 확인합니다...
    docker ps | findstr erp-postgres > nul
    if errorlevel 1 (
        echo 🚀 PostgreSQL Docker 컨테이너를 시작합니다...
        cd .. && docker-compose up -d postgres && cd backend
        echo ⏳ PostgreSQL이 준비될 때까지 잠시 대기합니다...
        timeout /t 10 > nul
    )
    echo 🔧 PostgreSQL로 서버를 시작합니다...
    echo 📍 pgAdmin: http://localhost:8081
    echo 📍 Swagger UI: http://localhost:8080/swagger-ui/index.html
    echo.
    call mvnw.cmd spring-boot:run
) else (
    echo ❌ 잘못된 선택입니다. 1 또는 2를 입력해주세요.
    pause
    exit /b 1
)

pause




