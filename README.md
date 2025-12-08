# ERP 시스템

통합 기업 자원 관리 시스템 (Enterprise Resource Planning System)

## 📋 프로젝트 개요

이 ERP 시스템은 중소기업을 위한 통합 관리 솔루션으로, 인사관리, 회계관리, 재고관리, 영업관리 모듈을 포함합니다.

### 주요 기능

- **인사관리**: 직원 정보, 부서 관리, 급여 관리
- **회계관리**: 계정과목, 전표 처리, 재무제표
- **재고관리**: 제품 관리, 재고 추적, 입출고 관리
- **영업관리**: 고객 관리, 주문 처리, 견적 관리

## 🏗️ 기술 스택

### 백엔드
- **Java 17** - 프로그래밍 언어
- **Spring Boot 3** - 애플리케이션 프레임워크
- **Spring Data JPA** - 데이터 액세스
- **PostgreSQL** - 메인 데이터베이스
- **Maven** - 빌드 도구

### 프론트엔드
- **React 18** - UI 라이브러리
- **TypeScript** - 타입 안전성
- **Vite** - 빌드 도구
- **Tailwind CSS** - 스타일링
- **React Query** - 상태 관리 및 데이터 페칭

### 개발 도구
- **Docker Compose** - 로컬 개발 환경
- **Swagger/OpenAPI** - API 문서화

## 🚀 시작하기

### 필수 요구사항

- Java 17 이상
- Node.js 18 이상
- Docker & Docker Compose
- Git

### 1. 프로젝트 클론

```bash
git clone <repository-url>
cd cursor-erp-system
```

### 2. 환경변수 설정

#### 백엔드 환경변수
`backend/src/main/resources/application-dev.yml.example` 파일을 참고하여 실제 설정 파일을 생성하세요:

```bash
# 개발 환경 설정 파일 생성
cp backend/src/main/resources/application-dev.yml.example backend/src/main/resources/application-dev.yml
```

주요 환경변수:
- `DB_URL`: 데이터베이스 연결 URL
- `DB_USERNAME`: 데이터베이스 사용자명
- `DB_PASSWORD`: 데이터베이스 비밀번호
- `JWT_SECRET`: JWT 토큰 시크릿 키
- `SERVER_PORT`: 서버 포트 (기본값: 8080)

#### 프론트엔드 환경변수
프론트엔드용 환경변수 파일을 생성하세요:

```bash
# 프론트엔드 환경변수 파일 생성
touch frontend/.env.development
```

주요 환경변수:
- `VITE_API_BASE_URL`: 백엔드 API URL (기본값: http://localhost:8080)
- `VITE_API_TIMEOUT`: API 요청 타임아웃 (기본값: 10000)
- `VITE_APP_ENV`: 애플리케이션 환경 (development/production)

### 2. 데이터베이스 설정

Docker Compose를 사용하여 PostgreSQL 데이터베이스를 실행합니다:

```bash
docker-compose up -d postgres
```

데이터베이스 초기화 스크립트 실행:

```bash
# PostgreSQL 컨테이너에 접속
docker exec -it erp-postgres psql -U cursor_erp_system -d cursor_erp_system

# 스키마 생성
\i /docker-entrypoint-initdb.d/01_create_database.sql
\i /docker-entrypoint-initdb.d/02_hr_tables.sql
\i /docker-entrypoint-initdb.d/03_inventory_tables.sql
\i /docker-entrypoint-initdb.d/04_sales_tables.sql
\i /docker-entrypoint-initdb.d/05_accounting_tables.sql

# 샘플 데이터 삽입
\i /docker-entrypoint-initdb.d/06_sample_data.sql
```

### 3. 백엔드 실행

```bash
cd backend
./mvnw spring-boot:run
```

백엔드 서버는 http://localhost:8080 에서 실행됩니다.

### 4. 프론트엔드 실행

새 터미널에서:

```bash
cd frontend
npm install
npm run dev
```

프론트엔드 서버는 http://localhost:3000 에서 실행됩니다.

## 📁 프로젝트 구조

```
cursor-erp-system/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/com/erp/
│   │   ├── common/            # 공통 유틸리티
│   │   ├── hr/                # 인사관리 모듈
│   │   ├── accounting/        # 회계관리 모듈
│   │   ├── inventory/         # 재고관리 모듈
│   │   ├── sales/             # 영업관리 모듈
│   │   └── config/            # 설정 클래스
│   ├── src/main/resources/
│   └── pom.xml
├── frontend/                   # React 프론트엔드
│   ├── src/
│   │   ├── components/        # 재사용 가능한 컴포넌트
│   │   ├── pages/             # 페이지 컴포넌트
│   │   ├── services/          # API 서비스
│   │   ├── types/             # TypeScript 타입 정의
│   │   └── lib/               # 유틸리티 함수
│   ├── package.json
│   └── vite.config.ts
├── database/                   # 데이터베이스 스크립트
│   ├── init/                  # 초기화 스크립트
│   ├── tables/                # 테이블 생성 스크립트
│   └── seed/                  # 샘플 데이터
├── docker-compose.yml
└── README.md
```

## 🔗 주요 엔드포인트

### API 문서
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/api-docs

### 데이터베이스 관리
- pgAdmin: http://localhost:8081
  - 이메일: admin@erp-system.com
  - 비밀번호: admin123
  - 데이터베이스: cursor_erp_system
  - 사용자명: cursor_erp_system

### 주요 API 엔드포인트

#### 인사관리
- `GET /api/hr/employees` - 직원 목록 조회
- `POST /api/hr/employees` - 직원 등록
- `PUT /api/hr/employees/{id}` - 직원 정보 수정
- `DELETE /api/hr/employees/{id}` - 직원 삭제

#### 재고관리
- `GET /api/inventory/products` - 제품 목록 조회
- `POST /api/inventory/products` - 제품 등록

#### 영업관리
- `GET /api/sales/customers` - 고객 목록 조회
- `POST /api/sales/orders` - 주문 생성

#### 회계관리
- `GET /api/accounting/accounts` - 계정과목 조회
- `POST /api/accounting/vouchers` - 전표 생성

## 🧪 테스트

### 백엔드 테스트
```bash
cd backend

# 단위 테스트 실행
./mvnw test

# 통합 테스트 실행
./mvnw verify

# 테스트 커버리지 포함 실행
./mvnw clean test jacoco:report
```

### 프론트엔드 테스트
```bash
cd frontend

# 단위 테스트 실행
npm run test

# 테스트 UI 실행
npm run test:ui

# 커버리지 포함 테스트
npm run test:coverage

# 타입 체크
npm run type-check

# 린팅 검사
npm run lint

# 코드 포맷팅
npm run format
```

## 📝 개발 가이드

### 코딩 규칙

#### 백엔드 (Java)
- SOLID 원칙 준수
- 모든 클래스와 메서드에 한국어 주석 작성
- Entity는 @Data 사용, DTO는 record 사용
- 예외 처리는 GlobalExceptionHandler에서 통합 관리

#### 프론트엔드 (TypeScript)
- 함수형 컴포넌트 사용
- React Hook Form + Zod를 통한 폼 검증
- React Query를 통한 서버 상태 관리
- Tailwind CSS를 통한 스타일링

### Git 커밋 메시지 규칙
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅, 세미콜론 누락 등
refactor: 코드 리팩토링
test: 테스트 코드 추가
chore: 빌드 프로세스 또는 보조 도구 수정
```

## 🚀 배포

### 프로덕션 빌드

#### 백엔드
```bash
cd backend
./mvnw clean package -Pprod
```

#### 프론트엔드
```bash
cd frontend
npm run build
```

### Docker 이미지 빌드
```bash
# 백엔드 이미지 빌드
docker build -t erp-backend ./backend

# 프론트엔드 이미지 빌드
docker build -t erp-frontend ./frontend
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (\`git checkout -b feature/AmazingFeature\`)
3. Commit your Changes (\`git commit -m 'Add some AmazingFeature'\`)
4. Push to the Branch (\`git push origin feature/AmazingFeature\`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 \`LICENSE\` 파일을 참조하세요.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 다음으로 연락해 주세요:

- 이메일: dev@erp-system.com
- 이슈 트래커: [GitHub Issues](https://github.com/your-repo/issues)

## 📚 추가 자료

- [Spring Boot 문서](https://spring.io/projects/spring-boot)
- [React 문서](https://reactjs.org/docs)
- [PostgreSQL 문서](https://www.postgresql.org/docs/)
- [Tailwind CSS 문서](https://tailwindcss.com/docs)
