# 백엔드 Production Dockerfile
# Multi-stage build로 변경
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# 작업 디렉토리 설정
WORKDIR /app

# pom.xml과 소스 코드 복사
COPY pom.xml .
COPY src ./src

# 빌드 (테스트 스킵)
RUN mvn clean package -DskipTests && \
    ls -la /app/target/

# 실행 단계
FROM eclipse-temurin:17-jre-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계에서 생성된 WAR 파일 복사
COPY --from=build /app/target/*.war app.war

# 포트 노출
EXPOSE 8991

# Spring Boot WAR 파일 실행
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.war"]
