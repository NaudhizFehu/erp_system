# 로그인 테스트 가이드

## 📋 테스트 계정 정보

### 개발 환경 (IDE 실행 시)
- **admin 계정**: `admin` / `admin123` (ADMIN 역할)
- **user 계정**: `user` / `user123` (USER 역할)

### 테스트 환경 (JUnit 테스트)
- **admin 계정**: `admin` / `admin123` (ADMIN 역할)  
- **user 계정**: `user` / `user123` (USER 역할)

## 🚀 IDE에서 서버 실행 및 테스트

### 1. 백엔드 서버 실행
1. IDE에서 `ErpSystemApplication.java` 실행
2. 서버가 정상적으로 시작되면 콘솔에 로그인 계정 정보가 표시됩니다:
   ```
   ✅ 로그인 계정 정보:
      👤 admin 계정 - 사용자명: admin, 비밀번호: admin123, 역할: ADMIN
      👤 user 계정 - 사용자명: user, 비밀번호: user123, 역할: USER
   ```

### 2. API 테스트

#### 2.1 admin 계정 정보 확인
```bash
GET http://localhost:8080/api/auth/debug/admin-info
```

#### 2.2 admin 계정 로그인 테스트
```bash
POST http://localhost:8080/api/auth/debug/test-login
Content-Type: application/json

{
  "usernameOrEmail": "admin",
  "password": "admin123"
}
```

#### 2.3 admin 계정 실제 로그인
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "admin",
  "password": "admin123"
}
```

#### 2.4 user 계정 로그인 테스트
```bash
POST http://localhost:8080/api/auth/debug/test-login
Content-Type: application/json

{
  "usernameOrEmail": "user",
  "password": "user123"
}
```

#### 2.5 user 계정 실제 로그인
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "user",
  "password": "user123"
}
```

## 🧪 JUnit 테스트 실행

### 1. 전체 테스트 실행
```bash
# IDE에서 실행하거나
./gradlew test

# 또는 특정 테스트 클래스만 실행
./gradlew test --tests "com.erp.auth.*"
```

### 2. 개별 테스트 클래스 실행
- `SimpleLoginTest`: 기본적인 계정 정보 및 비밀번호 검증 테스트
- `LoginIntegrationTest`: 전체 로그인 플로우 통합 테스트

## 🔍 예상 결과

### 성공적인 로그인 응답
```json
{
  "success": true,
  "message": "로그인이 완료되었습니다",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "email": "admin@abc.com",
      "name": "관리자",
      "role": "ADMIN",
      "companyId": 1,
      "companyName": "ABC 기업",
      "departmentId": 1,
      "departmentName": "인사부",
      "lastLoginAt": "2023-12-01T10:30:00Z"
    }
  },
  "timestamp": "2023-12-01T10:30:00Z"
}
```

### 실패한 로그인 응답
```json
{
  "success": false,
  "message": "로그인에 실패했습니다",
  "error": "사용자명 또는 비밀번호가 올바르지 않습니다",
  "timestamp": "2023-12-01T10:30:00Z"
}
```

## 🐛 문제 해결

### 1. admin 계정을 찾을 수 없는 경우
- DdlForcer가 정상 실행되었는지 확인
- 데이터베이스 연결 상태 확인
- 콘솔 로그에서 "✅ 로그인 계정 정보" 메시지 확인

### 2. 비밀번호가 일치하지 않는 경우
- 비밀번호 인코딩/검증 로직 확인
- BCrypt 설정 확인
- 디버깅 API로 비밀번호 검증 결과 확인

### 3. 계정이 비활성화된 경우
- `isActive`, `isLocked`, `isDeleted` 상태 확인
- 회사/부서 상태 확인

### 4. 인증 실패의 경우
- Spring Security 설정 확인
- JWT 설정 확인
- 로그에서 상세한 오류 메시지 확인

## 📝 테스트 체크리스트

- [ ] 백엔드 서버 정상 시작
- [ ] 콘솔에 로그인 계정 정보 표시
- [ ] admin 계정 정보 확인 API 호출 성공
- [ ] admin 계정 로그인 테스트 API 호출 성공
- [ ] admin 계정 실제 로그인 API 호출 성공
- [ ] user 계정 로그인 테스트 API 호출 성공
- [ ] user 계정 실제 로그인 API 호출 성공
- [ ] 잘못된 비밀번호로 로그인 실패 확인
- [ ] JUnit 테스트 모두 통과

## 🔧 추가 디버깅

문제가 지속되는 경우 다음 정보를 확인하세요:

1. **서버 로그**: 상세한 오류 메시지
2. **데이터베이스**: users 테이블의 실제 데이터
3. **네트워크**: API 호출 시 HTTP 상태 코드
4. **브라우저 개발자 도구**: 요청/응답 상세 정보
