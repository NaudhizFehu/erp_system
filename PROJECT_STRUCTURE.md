# 📁 ERP 시스템 프로젝트 구조

이 문서는 현재 ERP 시스템의 실제 파일 구조를 반영합니다.

## 🏗️ 전체 프로젝트 구조

```
cursor-erp-system/
├── 📄 .cursorrules                    # Cursor IDE 개발 규칙
├── 📄 .gitignore                      # Git 무시 파일 설정
├── 📄 API_DOCUMENTATION.md            # API 문서
├── 📄 PROJECT_STRUCTURE.md            # 프로젝트 구조 문서 (현재 파일)
├── 📄 README.md                       # 프로젝트 메인 문서
├── 📄 docker-compose.yml              # Docker Compose 설정
├── 📂 backend/                        # Spring Boot 백엔드
│   ├── 📄 fix_success_calls.py        # 개발 도구 스크립트
│   ├── 📄 pom.xml                     # Maven 설정
│   ├── 📄 run-dev.bat                 # Windows 개발 실행 스크립트
│   ├── 📄 run-dev.sh                  # Linux/Mac 개발 실행 스크립트
│   ├── 📂 database/                   # 데이터베이스 관련 파일
│   │   ├── 📄 complete_schema.sql     # 완전한 스키마
│   │   └── 📄 setup.sql               # 설정 스크립트
│   ├── 📂 logs/                       # 애플리케이션 로그
│   │   ├── 📄 erp-system.log          # 현재 로그
│   │   ├── 📄 erp-system.log.2025-09-10.0.gz
│   │   ├── 📄 erp-system.log.2025-09-10.1.gz
│   │   ├── 📄 erp-system.log.2025-09-11.0.gz
│   │   ├── 📄 erp-system.log.2025-09-12.0.gz
│   │   ├── 📄 erp-system.log.2025-09-12.1.gz
│   │   └── 📄 erp-system.log.2025-09-12.2.gz
│   ├── 📂 src/main/java/com/erp/      # Java 소스 코드
│   │   ├── 📂 accounting/             # 회계관리 모듈
│   │   │   ├── 📂 controller/         # REST 컨트롤러
│   │   │   │   ├── 📄 AccountingController.java
│   │   │   │   └── 📄 FinancialReportController.java
│   │   │   ├── 📂 dto/                # 데이터 전송 객체
│   │   │   │   ├── 📄 AccountDto.java
│   │   │   │   ├── 📄 BalanceVerificationDto.java
│   │   │   │   ├── 📄 BudgetDto.java
│   │   │   │   ├── 📄 FinancialReportDto.java
│   │   │   │   ├── 📄 FinancialReportItemDto.java
│   │   │   │   ├── 📄 GeneralLedgerDto.java
│   │   │   │   ├── 📄 TransactionCreateDto.java
│   │   │   │   ├── 📄 TransactionDto.java
│   │   │   │   ├── 📄 TransactionStatisticsDto.java
│   │   │   │   └── 📄 TrialBalanceDto.java
│   │   │   ├── 📂 entity/             # JPA 엔티티
│   │   │   │   ├── 📄 Account.java
│   │   │   │   ├── 📄 Budget.java
│   │   │   │   ├── 📄 BudgetRevision.java
│   │   │   │   ├── 📄 FinancialReport.java
│   │   │   │   ├── 📄 FinancialReportItem.java
│   │   │   │   └── 📄 Transaction.java
│   │   │   ├── 📂 repository/         # 데이터 액세스 계층
│   │   │   │   ├── 📄 AccountRepository.java
│   │   │   │   ├── 📄 BudgetRepository.java
│   │   │   │   ├── 📄 FinancialReportRepository.java
│   │   │   │   └── 📄 TransactionRepository.java
│   │   │   └── 📂 service/            # 비즈니스 로직 계층
│   │   │       ├── 📄 AccountingService.java
│   │   │       ├── 📄 FinancialReportService.java
│   │   │       └── 📂 impl/
│   │   │           ├── 📄 AccountingServiceImpl.java
│   │   │           └── 📄 FinancialReportServiceImpl.java
│   │   ├── 📂 common/                 # 공통 모듈
│   │   │   ├── 📂 constants/          # 상수 정의
│   │   │   │   └── 📄 ErrorCode.java
│   │   │   ├── 📂 controller/         # 공통 컨트롤러
│   │   │   │   ├── 📄 AuthController.java
│   │   │   │   ├── 📄 CompanyController.java
│   │   │   │   ├── 📄 ExampleController.java
│   │   │   │   └── 📄 GlobalSearchController.java
│   │   │   ├── 📂 dto/                # 공통 DTO
│   │   │   │   ├── 📄 ApiResponse.java
│   │   │   │   ├── 📂 auth/           # 인증 관련 DTO
│   │   │   │   ├── 📄 CodeCreateDto.java
│   │   │   │   ├── 📄 CodeDto.java
│   │   │   │   ├── 📄 CodeUpdateDto.java
│   │   │   │   ├── 📄 CompanyCreateDto.java
│   │   │   │   ├── 📄 CompanyDto.java
│   │   │   │   ├── 📄 CompanyUpdateDto.java
│   │   │   │   ├── 📄 DepartmentCreateDto.java
│   │   │   │   ├── 📄 DepartmentDto.java
│   │   │   │   ├── 📄 DepartmentUpdateDto.java
│   │   │   │   ├── 📄 GlobalSearchDto.java
│   │   │   │   ├── 📄 UserCreateDto.java
│   │   │   │   ├── 📄 UserDto.java
│   │   │   │   └── 📄 UserUpdateDto.java
│   │   │   ├── 📂 entity/             # 공통 엔티티
│   │   │   │   ├── 📄 BaseEntity.java
│   │   │   │   ├── 📄 Code.java
│   │   │   │   ├── 📄 Company.java
│   │   │   │   ├── 📄 Department.java
│   │   │   │   └── 📄 User.java
│   │   │   ├── 📂 exception/          # 예외 처리
│   │   │   │   ├── 📄 AuthenticationException.java
│   │   │   │   ├── 📄 AuthorizationException.java
│   │   │   │   ├── 📄 BusinessException.java
│   │   │   │   ├── 📄 DuplicateException.java
│   │   │   │   ├── 📄 EntityNotFoundException.java
│   │   │   │   ├── 📄 GlobalExceptionHandler.java
│   │   │   │   └── 📄 ValidationException.java
│   │   │   ├── 📂 repository/         # 공통 레포지토리
│   │   │   │   ├── 📄 CodeRepository.java
│   │   │   │   ├── 📄 CompanyRepository.java
│   │   │   │   ├── 📄 DepartmentRepository.java
│   │   │   │   └── 📄 UserRepository.java
│   │   │   ├── 📂 security/           # 보안 설정
│   │   │   │   ├── 📄 CustomUserDetailsService.java
│   │   │   │   ├── 📄 JwtAuthenticationEntryPoint.java
│   │   │   │   ├── 📄 JwtAuthenticationFilter.java
│   │   │   │   ├── 📄 JwtUtils.java
│   │   │   │   └── 📄 UserPrincipal.java
│   │   │   ├── 📂 service/            # 공통 서비스
│   │   │   │   └── 📄 GlobalSearchService.java
│   │   │   └── 📂 utils/              # 유틸리티
│   │   │       └── 📄 ExceptionUtils.java
│   │   ├── 📂 config/                 # 설정 클래스
│   │   │   ├── 📄 DataInitializer.java
│   │   │   ├── 📄 DdlForcer.java
│   │   │   ├── 📄 DdlTester.java
│   │   │   ├── 📄 JpaConfig.java
│   │   │   ├── 📄 SecurityConfig.java
│   │   │   ├── 📄 SwaggerConfig.java
│   │   │   └── 📄 WebConfig.java
│   │   ├── 📂 dashboard/              # 대시보드 모듈
│   │   │   ├── 📂 controller/
│   │   │   │   └── 📄 DashboardController.java
│   │   │   ├── 📂 dto/
│   │   │   │   └── 📄 DashboardDto.java
│   │   │   └── 📂 service/
│   │   │       ├── 📄 DashboardService.java
│   │   │       └── 📂 impl/
│   │   │           └── 📄 DashboardServiceImpl.java
│   │   ├── 📄 ErpSystemApplication.java # 메인 애플리케이션 클래스
│   │   ├── 📂 hr/                     # 인사관리 모듈
│   │   │   ├── 📂 controller/
│   │   │   │   ├── 📄 EmployeeController.java
│   │   │   │   └── 📄 PositionController.java
│   │   │   ├── 📂 dto/
│   │   │   │   ├── 📄 AttendanceDto.java
│   │   │   │   ├── 📄 DepartmentDto.java
│   │   │   │   ├── 📄 EmployeeCreateDto.java
│   │   │   │   ├── 📄 EmployeeDto.java
│   │   │   │   ├── 📄 EmployeeUpdateDto.java
│   │   │   │   ├── 📄 PositionCreateDto.java
│   │   │   │   ├── 📄 PositionDto.java
│   │   │   │   ├── 📄 PositionUpdateDto.java
│   │   │   │   └── 📄 SalaryDto.java
│   │   │   ├── 📂 entity/
│   │   │   │   ├── 📄 Attendance.java
│   │   │   │   ├── 📄 Employee.java
│   │   │   │   ├── 📄 Position.java
│   │   │   │   └── 📄 Salary.java
│   │   │   ├── 📂 repository/
│   │   │   │   ├── 📄 AttendanceRepository.java
│   │   │   │   ├── 📄 EmployeeRepository.java
│   │   │   │   ├── 📄 PositionRepository.java
│   │   │   │   └── 📄 SalaryRepository.java
│   │   │   └── 📂 service/
│   │   │       ├── 📄 EmployeeService.java
│   │   │       ├── 📂 impl/
│   │   │       │   ├── 📄 EmployeeServiceImpl.java
│   │   │       │   └── 📄 PositionServiceImpl.java
│   │   │       └── 📄 PositionService.java
│   │   ├── 📂 inventory/              # 재고관리 모듈
│   │   │   ├── 📂 controller/
│   │   │   │   ├── 📄 InventoryController.java
│   │   │   │   └── 📄 ProductController.java
│   │   │   ├── 📂 dto/
│   │   │   │   ├── 📄 InventoryDto.java
│   │   │   │   ├── 📄 ProductCategoryDto.java
│   │   │   │   ├── 📄 ProductDto.java
│   │   │   │   ├── 📄 StockMovementDto.java
│   │   │   │   └── 📄 WarehouseDto.java
│   │   │   ├── 📂 entity/
│   │   │   │   ├── 📄 Inventory.java
│   │   │   │   ├── 📄 Product.java
│   │   │   │   ├── 📄 ProductCategory.java
│   │   │   │   ├── 📄 StockMovement.java
│   │   │   │   └── 📄 Warehouse.java
│   │   │   ├── 📂 repository/
│   │   │   │   ├── 📄 InventoryRepository.java
│   │   │   │   ├── 📄 ProductCategoryRepository.java
│   │   │   │   ├── 📄 ProductRepository.java
│   │   │   │   ├── 📄 StockMovementRepository.java
│   │   │   │   └── 📄 WarehouseRepository.java
│   │   │   └── 📂 service/
│   │   │       ├── 📄 InventoryService.java
│   │   │       ├── 📄 ProductCategoryService.java
│   │   │       ├── 📄 ProductService.java
│   │   │       ├── 📄 StockMovementService.java
│   │   │       ├── 📄 WarehouseService.java
│   │   │       └── 📂 impl/
│   │   │           ├── 📄 InventoryServiceImpl.java
│   │   │           ├── 📄 ProductCategoryServiceImpl.java
│   │   │           ├── 📄 ProductServiceImpl.java
│   │   │           ├── 📄 StockMovementServiceImpl.java
│   │   │           └── 📄 WarehouseServiceImpl.java
│   │   ├── 📂 sales/                  # 영업관리 모듈
│   │   │   ├── 📂 controller/
│   │   │   │   ├── 📄 CustomerController.java
│   │   │   │   └── 📄 OrderController.java
│   │   │   ├── 📂 dto/
│   │   │   │   ├── 📄 CustomerDto.java
│   │   │   │   ├── 📄 OrderDto.java
│   │   │   │   ├── 📄 OrderItemDto.java
│   │   │   │   ├── 📄 QuoteDto.java
│   │   │   │   └── 📄 SalesReportDto.java
│   │   │   ├── 📂 entity/
│   │   │   │   ├── 📄 Customer.java
│   │   │   │   ├── 📄 Order.java
│   │   │   │   ├── 📄 OrderItem.java
│   │   │   │   ├── 📄 Quote.java
│   │   │   │   ├── 📄 SalesReport.java
│   │   │   │   └── 📄 SalesTarget.java
│   │   │   ├── 📂 repository/
│   │   │   │   ├── 📄 CustomerRepository.java
│   │   │   │   ├── 📄 OrderRepository.java
│   │   │   │   ├── 📄 QuoteRepository.java
│   │   │   │   └── 📄 SalesReportRepository.java
│   │   │   └── 📂 service/
│   │   │       ├── 📄 CustomerService.java
│   │   │       ├── 📄 OrderService.java
│   │   │       ├── 📄 QuoteService.java
│   │   │       ├── 📄 SalesReportService.java
│   │   │       └── 📂 impl/
│   │   │           ├── 📄 CustomerServiceImpl.java
│   │   │           ├── 📄 OrderServiceImpl.java
│   │   │           ├── 📄 QuoteServiceImpl.java
│   │   │           └── 📄 SalesReportServiceImpl.java
│   │   └── 📂 util/                   # 유틸리티 클래스
│   ├── 📂 src/main/resources/         # 리소스 파일
│   │   ├── 📄 application-dev.yml     # 개발 환경 설정
│   │   ├── 📄 application-prod.yml    # 운영 환경 설정
│   │   ├── 📄 application.yml         # 기본 설정
│   │   ├── 📄 data_corrected.sql      # 수정된 데이터
│   │   ├── 📄 data.sql                # 초기 데이터
│   │   ├── 📂 database/               # 데이터베이스 스크립트
│   │   │   └── 📄 complete_schema.sql # 완전한 스키마
│   │   ├── 📄 import.sql              # 데이터 임포트 스크립트
│   │   ├── 📄 schema.sql              # 스키마 생성 스크립트
│   │   └── 📂 static/                 # 정적 리소스
│   │       └── 📄 api-docs.html       # API 문서
│   └── 📂 target/                     # Maven 빌드 결과 (Git에서 제외됨)
│       ├── 📂 classes/                # 컴파일된 클래스 파일
│       ├── 📂 generated-sources/      # 생성된 소스 파일
│       ├── 📂 generated-test-sources/ # 생성된 테스트 소스
│       └── 📂 test-classes/           # 컴파일된 테스트 클래스
├── 📂 database/                       # 데이터베이스 스크립트
│   ├── 📂 init/                       # 초기화 스크립트
│   │   └── 📄 01_create_database.sql  # 데이터베이스 생성
│   ├── 📂 seed/                       # 샘플 데이터
│   │   └── 📄 06_sample_data.sql      # 샘플 데이터 삽입
│   └── 📂 tables/                     # 테이블 생성 스크립트
│       ├── 📄 02_hr_tables.sql        # 인사관리 테이블
│       ├── 📄 03_inventory_tables.sql # 재고관리 테이블
│       ├── 📄 04_sales_tables.sql     # 영업관리 테이블
│       └── 📄 05_accounting_tables.sql # 회계관리 테이블
└── 📂 frontend/                       # React 프론트엔드
    ├── 📄 index.html                  # HTML 엔트리 포인트
    ├── 📄 package-lock.json           # npm 락 파일
    ├── 📄 package.json                # npm 패키지 설정
    ├── 📄 postcss.config.js           # PostCSS 설정
    ├── 📄 tailwind.config.js          # Tailwind CSS 설정
    ├── 📄 tsconfig.json               # TypeScript 설정
    ├── 📄 tsconfig.node.json          # Node.js용 TypeScript 설정
    ├── 📄 vite.config.ts              # Vite 설정
    ├── 📄 vitest.config.ts            # Vitest 설정
    ├── 📂 node_modules/               # npm 의존성 (Git에서 제외됨)
    └── 📂 src/                        # 소스 코드
        ├── 📄 App.tsx                 # 메인 앱 컴포넌트
        ├── 📄 main.tsx                # 앱 엔트리 포인트
        ├── 📄 index.css               # 글로벌 스타일
        ├── 📄 vite-env.d.ts           # Vite 타입 정의
        ├── 📂 components/             # 재사용 가능한 컴포넌트
        │   ├── 📂 accounting/         # 회계관리 컴포넌트
        │   │   └── 📄 AccountingDashboard.tsx
        │   ├── 📂 auth/               # 인증 컴포넌트
        │   │   └── 📄 ProtectedRoute.tsx
        │   ├── 📂 common/             # 공통 컴포넌트
        │   │   └── 📄 LoadingSpinner.tsx
        │   ├── 📂 dashboard/          # 대시보드 컴포넌트
        │   │   ├── 📂 charts/         # 차트 컴포넌트
        │   │   │   └── 📄 RevenueChart.tsx
        │   │   ├── 📄 DashboardCustomizer.tsx
        │   │   └── 📂 widgets/        # 위젯 컴포넌트
        │   │       ├── 📄 ActivityWidget.tsx
        │   │       ├── 📄 OverviewWidget.tsx
        │   │       └── 📄 TodoWidget.tsx
        │   ├── 📂 hr/                 # 인사관리 컴포넌트
        │   │   ├── 📄 EmployeeCard.tsx
        │   │   ├── 📄 EmployeeForm.tsx
        │   │   └── 📄 EmployeeTable.tsx
        │   ├── 📂 inventory/          # 재고관리 컴포넌트
        │   │   ├── 📄 InventoryDashboard.tsx
        │   │   ├── 📄 ProductForm.tsx
        │   │   └── 📄 ProductTable.tsx
        │   ├── 📂 layout/             # 레이아웃 컴포넌트
        │   │   ├── 📄 Header.tsx
        │   │   ├── 📄 Layout.tsx
        │   │   └── 📄 Sidebar.tsx
        │   ├── 📂 notification/       # 알림 컴포넌트
        │   │   └── 📄 NotificationDropdown.tsx
        │   ├── 📂 sales/              # 영업관리 컴포넌트
        │   │   ├── 📄 CustomerCard.tsx
        │   │   └── 📄 CustomerTable.tsx
        │   ├── 📂 search/             # 검색 컴포넌트
        │   │   └── 📄 GlobalSearch.tsx
        │   └── 📂 ui/                 # UI 컴포넌트 라이브러리
        │       ├── 📄 alert-dialog.tsx
        │       ├── 📄 alert.tsx
        │       ├── 📄 avatar.tsx
        │       ├── 📄 badge.tsx
        │       ├── 📄 button.tsx
        │       ├── 📄 calendar.tsx
        │       ├── 📄 card.tsx
        │       ├── 📄 checkbox.tsx
        │       ├── 📄 command.tsx
        │       ├── 📄 dialog.tsx
        │       ├── 📄 dropdown-menu.tsx
        │       ├── 📄 form.tsx
        │       ├── 📄 input.tsx
        │       ├── 📄 label.tsx
        │       ├── 📄 popover.tsx
        │       ├── 📄 progress.tsx
        │       ├── 📄 scroll-area.tsx
        │       ├── 📄 select.tsx
        │       ├── 📄 separator.tsx
        │       ├── 📄 slider.tsx
        │       ├── 📄 switch.tsx
        │       ├── 📄 table.tsx
        │       ├── 📄 tabs.tsx
        │       └── 📄 textarea.tsx
        ├── 📂 config/                 # 설정 파일
        │   └── 📄 env.ts              # 환경 변수 설정
        ├── 📂 contexts/               # React Context
        │   └── 📄 AuthContext.tsx     # 인증 컨텍스트
        ├── 📂 hooks/                  # 커스텀 훅
        │   ├── 📄 useAccounting.ts    # 회계관리 훅
        │   ├── 📄 useDashboard.ts     # 대시보드 훅
        │   ├── 📄 useDebounce.ts      # 디바운스 훅
        │   ├── 📄 useEmployees.ts     # 직원관리 훅
        │   ├── 📄 useInventory.ts     # 재고관리 훅
        │   └── 📄 useSales.ts         # 영업관리 훅
        ├── 📂 lib/                    # 라이브러리
        │   └── 📄 utils.ts            # 유틸리티 함수
        ├── 📂 mocks/                  # 목 데이터
        │   └── 📄 hrMockData.ts       # 인사관리 목 데이터
        ├── 📂 pages/                  # 페이지 컴포넌트
        │   ├── 📂 accounting/         # 회계관리 페이지
        │   │   └── 📄 AccountList.tsx
        │   ├── 📂 auth/               # 인증 페이지
        │   │   └── 📄 LoginPage.tsx
        │   ├── 📂 company/            # 회사 관리 페이지
        │   │   ├── 📄 CompanyDetail.tsx
        │   │   └── 📄 CompanyList.tsx
        │   ├── 📂 dashboard/          # 대시보드 페이지
        │   │   └── 📄 MainDashboard.tsx
        │   ├── 📄 Dashboard.tsx       # 대시보드 (레거시)
        │   ├── 📂 hr/                 # 인사관리 페이지
        │   │   ├── 📄 DepartmentDetail.tsx
        │   │   ├── 📄 EmployeeDetail.tsx
        │   │   ├── 📄 EmployeeForm.tsx
        │   │   ├── 📄 EmployeeList.tsx
        │   │   └── 📄 EmployeeManagement.tsx
        │   ├── 📂 inventory/          # 재고관리 페이지
        │   │   ├── 📄 InventoryDashboardPage.tsx
        │   │   ├── 📄 ProductDetail.tsx
        │   │   ├── 📄 ProductList.tsx
        │   │   └── 📄 ProductManagementPage.tsx
        │   └── 📂 sales/              # 영업관리 페이지
        │       ├── 📄 CustomerDetail.tsx
        │       ├── 📄 CustomerList.tsx
        │       └── 📄 OrderDetail.tsx
        ├── 📂 services/               # API 서비스
        │   ├── 📄 accountingApi.ts    # 회계관리 API
        │   ├── 📄 api.ts              # 기본 API 설정
        │   ├── 📄 authApi.ts          # 인증 API
        │   ├── 📄 customerService.ts  # 고객 서비스
        │   ├── 📄 dashboardApi.ts     # 대시보드 API
        │   ├── 📄 departmentService.ts # 부서 서비스
        │   ├── 📄 employeeService.ts  # 직원 서비스
        │   ├── 📄 hrApi.ts            # 인사관리 API
        │   ├── 📄 hrMockApi.ts        # 인사관리 목 API
        │   ├── 📄 inventoryApi.ts     # 재고관리 API
        │   ├── 📄 orderService.ts     # 주문 서비스
        │   ├── 📄 productService.ts   # 제품 서비스
        │   └── 📄 salesApi.ts         # 영업관리 API
        ├── 📂 test/                   # 테스트 파일
        │   └── 📄 setup.ts            # 테스트 설정
        ├── 📂 types/                  # TypeScript 타입 정의
        │   ├── 📄 accounting.ts       # 회계관리 타입
        │   ├── 📄 auth.ts             # 인증 타입
        │   ├── 📄 common.ts           # 공통 타입
        │   ├── 📄 dashboard.ts        # 대시보드 타입
        │   ├── 📄 hr.ts               # 인사관리 타입
        │   ├── 📄 inventory.ts        # 재고관리 타입
        │   └── 📄 sales.ts            # 영업관리 타입
        └── 📂 utils/                  # 유틸리티
            └── 📄 format.ts           # 포맷팅 유틸리티
```

## 📊 프로젝트 구조 통계

### 📁 디렉토리 개수
- **백엔드 Java 패키지**: 5개 (accounting, common, dashboard, hr, inventory, sales)
- **프론트엔드 컴포넌트**: 8개 (accounting, auth, common, dashboard, hr, inventory, layout, notification, sales, search, ui)
- **데이터베이스 스크립트**: 3개 (init, seed, tables)

### 📄 파일 개수 (대략)
- **Java 파일**: 약 158개
- **TypeScript/React 파일**: 약 100개
- **설정 파일**: 약 20개
- **데이터베이스 스크립트**: 8개

## 🏗️ 아키텍처 패턴

### 백엔드 (Spring Boot)
```
Controller → Service → Repository → Entity
    ↓         ↓          ↓
   DTO ←→ Business Logic ←→ Database
```

### 프론트엔드 (React)
```
Pages → Components → Hooks → Services → API
  ↓         ↓         ↓        ↓
UI Logic ← State ← Data Fetch ← Backend
```

## 🔧 주요 설정 파일

### 백엔드 설정
- `pom.xml` - Maven 의존성 및 빌드 설정
- `application.yml` - Spring Boot 기본 설정
- `application-dev.yml` - 개발 환경 설정
- `application-prod.yml` - 운영 환경 설정

### 프론트엔드 설정
- `package.json` - npm 의존성 및 스크립트
- `vite.config.ts` - Vite 빌드 도구 설정
- `tailwind.config.js` - Tailwind CSS 설정
- `tsconfig.json` - TypeScript 컴파일러 설정

### 데이터베이스 설정
- `01_create_database.sql` - 데이터베이스 생성
- `02_hr_tables.sql` - 인사관리 테이블
- `03_inventory_tables.sql` - 재고관리 테이블
- `04_sales_tables.sql` - 영업관리 테이블
- `05_accounting_tables.sql` - 회계관리 테이블
- `06_sample_data.sql` - 샘플 데이터

## 📝 참고사항

1. **Git에서 제외되는 파일들**:
   - `backend/target/` - Maven 빌드 결과
   - `frontend/node_modules/` - npm 의존성
   - `backend/logs/` - 애플리케이션 로그
   - 모든 `.log` 파일

2. **개발 도구**:
   - `.cursorrules` - Cursor IDE 개발 규칙
   - `fix_success_calls.py` - 개발 도구 스크립트
   - `run-dev.bat/sh` - 개발 환경 실행 스크립트

3. **문서화**:
   - `README.md` - 프로젝트 메인 문서
   - `API_DOCUMENTATION.md` - API 상세 문서
   - `PROJECT_STRUCTURE.md` - 프로젝트 구조 문서 (현재 파일)

---

*이 문서는 프로젝트의 실제 파일 구조를 반영하며, 개발 과정에서 업데이트될 수 있습니다.*

