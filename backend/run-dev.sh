#!/bin/bash

# ERP 시스템 개발 서버 실행 스크립트

echo "🚀 ERP 시스템 백엔드 개발 서버를 시작합니다..."

# Java 버전 확인
echo "☕ Java 버전 확인:"
java -version

# Maven 버전 확인
echo "📦 Maven 버전 확인:"
./mvnw -version

# 데이터베이스 선택
echo ""
echo "📊 데이터베이스를 선택하세요:"
echo "1) H2 데이터베이스 (개발용, 권장)"
echo "2) PostgreSQL (Docker 필요)"
read -p "선택 (1 또는 2): " db_choice

if [ "$db_choice" = "1" ]; then
    echo "🔧 H2 데이터베이스로 개발 서버를 시작합니다..."
    echo "📍 H2 콘솔: http://localhost:8080/h2-console"
    echo "📍 Swagger UI: http://localhost:8080/swagger-ui/index.html"
    echo ""
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
elif [ "$db_choice" = "2" ]; then
    echo "🐳 PostgreSQL Docker 컨테이너를 확인합니다..."
    if ! docker ps | grep -q erp-postgres; then
        echo "🚀 PostgreSQL Docker 컨테이너를 시작합니다..."
        cd .. && docker-compose up -d postgres && cd backend
        echo "⏳ PostgreSQL이 준비될 때까지 잠시 대기합니다..."
        sleep 10
    fi
    echo "🔧 PostgreSQL로 서버를 시작합니다..."
    echo "📍 pgAdmin: http://localhost:8081"
    echo "📍 Swagger UI: http://localhost:8080/swagger-ui/index.html"
    echo ""
    ./mvnw spring-boot:run
else
    echo "❌ 잘못된 선택입니다. 1 또는 2를 입력해주세요."
    exit 1
fi




