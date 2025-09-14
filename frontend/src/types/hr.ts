/**
 * HR 모듈 TypeScript 타입 정의
 * 백엔드 DTO와 매칭되는 인터페이스들을 정의합니다
 */

// ApiResponse는 @/services/api.ts에서 import하여 사용

/**
 * 페이지네이션 응답 타입
 */
export interface PageResponse<T> {
  content: T[]
  pageable: {
    sort: {
      sorted: boolean
      unsorted: boolean
      empty: boolean
    }
    pageNumber: number
    pageSize: number
    offset: number
    paged: boolean
    unpaged: boolean
  }
  totalElements: number
  totalPages: number
  last: boolean
  first: boolean
  numberOfElements: number
  size: number
  number: number
  sort: {
    sorted: boolean
    unsorted: boolean
    empty: boolean
  }
  empty: boolean
}

/**
 * 회사 정보 타입
 */
export interface Company {
  id: number
  companyCode: string
  companyName: string
  businessNumber: string
  address: string
  createdAt: string
  updatedAt: string
}

/**
 * 부서 정보 타입
 */
export interface Department {
  id: number
  departmentCode: string
  departmentName: string
  parentDepartment?: Department
  manager?: Employee
  createdAt: string
  updatedAt: string
}

/**
 * 직급 정보 타입
 */
export interface Position {
  id: number
  positionCode: string
  name: string
  nameEn?: string
  description?: string
  company: Company
  positionLevel: number
  positionCategory?: PositionCategory
  positionType?: PositionType
  minSalary?: number
  maxSalary?: number
  sortOrder: number
  isActive: boolean
  promotionTargets?: string
  requirements?: string
  authorities?: string
  employeeCount: number
  createdAt: string
  updatedAt: string
}

/**
 * 직급 분류 열거형
 */
export enum PositionCategory {
  EXECUTIVE = 'EXECUTIVE',
  MANAGEMENT = 'MANAGEMENT', 
  SENIOR = 'SENIOR',
  JUNIOR = 'JUNIOR',
  INTERN = 'INTERN'
}

/**
 * 직급 유형 열거형
 */
export enum PositionType {
  PERMANENT = 'PERMANENT',
  CONTRACT = 'CONTRACT',
  TEMPORARY = 'TEMPORARY',
  CONSULTANT = 'CONSULTANT'
}

/**
 * 직원 정보 타입
 */
export interface Employee {
  id: number
  employeeNumber: string
  name: string
  nameEn?: string
  email: string
  phone?: string
  mobile?: string
  birthDate?: string
  gender?: Gender
  address?: string
  addressDetail?: string
  postalCode?: string
  company: Company
  department: Department
  position: Position
  hireDate: string
  terminationDate?: string
  employmentStatus: EmploymentStatus
  employmentType?: EmploymentType
  baseSalary?: number
  bankName?: string
  accountNumber?: string
  accountHolder?: string
  emergencyContact?: string
  emergencyRelation?: string
  education?: string
  major?: string
  career?: string
  skills?: string
  certifications?: string
  memo?: string
  profileImageUrl?: string
  yearsOfService: number
  age: number
  createdAt: string
  updatedAt: string
}

/**
 * 성별 열거형
 */
export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE'
}

/**
 * 근무 상태 열거형
 */
export enum EmploymentStatus {
  ACTIVE = 'ACTIVE',
  ON_LEAVE = 'ON_LEAVE',
  TERMINATED = 'TERMINATED',
  SUSPENDED = 'SUSPENDED'
}

/**
 * 고용 형태 열거형
 */
export enum EmploymentType {
  FULL_TIME = 'FULL_TIME',
  CONTRACT = 'CONTRACT',
  PART_TIME = 'PART_TIME',
  INTERN = 'INTERN',
  FREELANCER = 'FREELANCER'
}

/**
 * 근태 정보 타입
 */
export interface Attendance {
  id: number
  employee: Employee
  attendanceDate: string
  checkInTime?: string
  checkOutTime?: string
  breakStartTime?: string
  breakEndTime?: string
  workMinutes?: number
  overtimeMinutes: number
  nightWorkMinutes: number
  holidayWorkMinutes: number
  lateMinutes: number
  earlyLeaveMinutes: number
  attendanceStatus: AttendanceStatus
  workType: WorkType
  checkInLocation?: string
  checkOutLocation?: string
  remarks?: string
  approvedBy?: Employee
  approvedAt?: string
  approvalStatus: ApprovalStatus
  workTimeFormatted: string
  overtimeFormatted: string
  isNormalAttendance: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 근태 상태 열거형
 */
export enum AttendanceStatus {
  PRESENT = 'PRESENT',
  ABSENT = 'ABSENT',
  LATE = 'LATE',
  EARLY_LEAVE = 'EARLY_LEAVE',
  HALF_DAY = 'HALF_DAY',
  FULL_DAY_OFF = 'FULL_DAY_OFF',
  SICK_LEAVE = 'SICK_LEAVE',
  PERSONAL_LEAVE = 'PERSONAL_LEAVE',
  BUSINESS_TRIP = 'BUSINESS_TRIP',
  EDUCATION = 'EDUCATION',
  HOLIDAY = 'HOLIDAY'
}

/**
 * 근무 유형 열거형
 */
export enum WorkType {
  OFFICE = 'OFFICE',
  REMOTE = 'REMOTE',
  HYBRID = 'HYBRID',
  FIELD = 'FIELD',
  BUSINESS_TRIP = 'BUSINESS_TRIP'
}

/**
 * 승인 상태 열거형
 */
export enum ApprovalStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

/**
 * 급여 정보 타입
 */
export interface Salary {
  id: number
  employee: Employee
  payYear: number
  payMonth: number
  payDate?: string
  baseSalary: number
  positionAllowance: number
  mealAllowance: number
  transportAllowance: number
  familyAllowance: number
  overtimeAllowance: number
  holidayAllowance: number
  nightAllowance: number
  otherAllowance: number
  performanceBonus: number
  specialAllowance: number
  grossPay: number
  nationalPension: number
  healthInsurance: number
  longTermCare: number
  employmentInsurance: number
  incomeTax: number
  localIncomeTax: number
  otherDeduction: number
  totalDeduction: number
  netPay: number
  workDays?: number
  workHours?: number
  overtimeHours: number
  nightHours: number
  holidayHours: number
  paymentStatus: PaymentStatus
  salaryType: SalaryType
  calculationDate?: string
  approvedBy?: Employee
  approvedAt?: string
  remarks?: string
  payrollTitle: string
  isMonthlySalary: boolean
  isPaid: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 지급 상태 열거형
 */
export enum PaymentStatus {
  PENDING = 'PENDING',
  CALCULATED = 'CALCULATED',
  APPROVED = 'APPROVED',
  PAID = 'PAID',
  CANCELLED = 'CANCELLED'
}

/**
 * 급여 유형 열거형
 */
export enum SalaryType {
  MONTHLY = 'MONTHLY',
  HOURLY = 'HOURLY',
  DAILY = 'DAILY',
  BONUS = 'BONUS',
  SEVERANCE = 'SEVERANCE'
}

/**
 * 직원 생성 요청 타입
 */
export interface EmployeeCreateRequest {
  employeeNumber: string
  name: string
  nameEn?: string
  email: string
  phone?: string
  mobile?: string
  residentNumber?: string
  birthDate?: string
  gender?: Gender
  address?: string
  addressDetail?: string
  postalCode?: string
  companyId: number
  departmentId: number
  positionId: number
  hireDate: string
  employmentStatus?: EmploymentStatus
  employmentType?: EmploymentType
  baseSalary?: number
  bankName?: string
  accountNumber?: string
  accountHolder?: string
  emergencyContact?: string
  emergencyRelation?: string
  education?: string
  major?: string
  career?: string
  skills?: string
  certifications?: string
  memo?: string
  profileImageUrl?: string
}

/**
 * 직원 수정 요청 타입
 */
export interface EmployeeUpdateRequest {
  name?: string
  nameEn?: string
  email?: string
  phone?: string
  mobile?: string
  birthDate?: string
  gender?: Gender
  address?: string
  addressDetail?: string
  postalCode?: string
  departmentId?: number
  positionId?: number
  employmentStatus?: EmploymentStatus
  employmentType?: EmploymentType
  baseSalary?: number
  bankName?: string
  accountNumber?: string
  accountHolder?: string
  emergencyContact?: string
  emergencyRelation?: string
  education?: string
  major?: string
  career?: string
  skills?: string
  certifications?: string
  memo?: string
  profileImageUrl?: string
}

/**
 * 직급 생성 요청 타입
 */
export interface PositionCreateRequest {
  positionCode: string
  name: string
  nameEn?: string
  description?: string
  companyId: number
  positionLevel: number
  positionCategory?: PositionCategory
  positionType?: PositionType
  minSalary?: number
  maxSalary?: number
  sortOrder?: number
  isActive?: boolean
  promotionTargets?: string
  requirements?: string
  authorities?: string
}

/**
 * 직급 수정 요청 타입
 */
export interface PositionUpdateRequest {
  name?: string
  nameEn?: string
  description?: string
  positionLevel?: number
  positionCategory?: PositionCategory
  positionType?: PositionType
  minSalary?: number
  maxSalary?: number
  sortOrder?: number
  isActive?: boolean
  promotionTargets?: string
  requirements?: string
  authorities?: string
}

/**
 * 근태 생성 요청 타입
 */
export interface AttendanceCreateRequest {
  employeeId: number
  attendanceDate: string
  checkInTime?: string
  checkOutTime?: string
  breakStartTime?: string
  breakEndTime?: string
  attendanceStatus: AttendanceStatus
  workType?: WorkType
  checkInLocation?: string
  checkOutLocation?: string
  remarks?: string
}

/**
 * 급여 생성 요청 타입
 */
export interface SalaryCreateRequest {
  employeeId: number
  payYear: number
  payMonth: number
  baseSalary: number
  positionAllowance?: number
  mealAllowance?: number
  transportAllowance?: number
  familyAllowance?: number
  overtimeAllowance?: number
  holidayAllowance?: number
  nightAllowance?: number
  otherAllowance?: number
  performanceBonus?: number
  specialAllowance?: number
  workDays?: number
  workHours?: number
  overtimeHours?: number
  nightHours?: number
  holidayHours?: number
  salaryType?: SalaryType
  remarks?: string
}

/**
 * 검색 파라미터 타입
 */
export interface SearchParams {
  searchTerm?: string
  page?: number
  size?: number
  sort?: string
  companyId?: number
  departmentId?: number
  positionId?: number
  employmentStatus?: EmploymentStatus
  startDate?: string
  endDate?: string
}

/**
 * 통계 데이터 타입
 */
export interface StatisticsData {
  label: string
  count: number
  percentage?: number
}

/**
 * 한국어 라벨 매핑
 */
export const KOREAN_LABELS = {
  // 성별
  [Gender.MALE]: '남성',
  [Gender.FEMALE]: '여성',
  
  // 근무 상태
  [EmploymentStatus.ACTIVE]: '재직',
  [EmploymentStatus.ON_LEAVE]: '휴직',
  [EmploymentStatus.TERMINATED]: '퇴직',
  [EmploymentStatus.SUSPENDED]: '정직',
  
  // 고용 형태
  [EmploymentType.FULL_TIME]: '정규직',
  [EmploymentType.CONTRACT]: '계약직',
  [EmploymentType.PART_TIME]: '시간제',
  [EmploymentType.INTERN]: '인턴',
  [EmploymentType.FREELANCER]: '프리랜서',
  
  // 직급 분류
  [PositionCategory.EXECUTIVE]: '임원',
  [PositionCategory.MANAGEMENT]: '관리직',
  [PositionCategory.SENIOR]: '선임',
  [PositionCategory.JUNIOR]: '주니어',
  [PositionCategory.INTERN]: '인턴',
  
  // 직급 유형
  [PositionType.PERMANENT]: '정규직',
  [PositionType.CONTRACT]: '계약직',
  [PositionType.TEMPORARY]: '임시직',
  [PositionType.CONSULTANT]: '컨설턴트',
  
  // 근태 상태
  [AttendanceStatus.PRESENT]: '출근',
  [AttendanceStatus.ABSENT]: '결근',
  [AttendanceStatus.LATE]: '지각',
  [AttendanceStatus.EARLY_LEAVE]: '조퇴',
  [AttendanceStatus.HALF_DAY]: '반차',
  [AttendanceStatus.FULL_DAY_OFF]: '연차',
  [AttendanceStatus.SICK_LEAVE]: '병가',
  [AttendanceStatus.PERSONAL_LEAVE]: '개인사유',
  [AttendanceStatus.BUSINESS_TRIP]: '출장',
  [AttendanceStatus.EDUCATION]: '교육',
  [AttendanceStatus.HOLIDAY]: '휴일',
  
  // 근무 유형
  [WorkType.OFFICE]: '사무실 근무',
  [WorkType.REMOTE]: '재택근무',
  [WorkType.HYBRID]: '하이브리드',
  [WorkType.FIELD]: '현장근무',
  [WorkType.BUSINESS_TRIP]: '출장',
  
  // 승인 상태
  [ApprovalStatus.PENDING]: '승인대기',
  [ApprovalStatus.APPROVED]: '승인',
  [ApprovalStatus.REJECTED]: '반려',
  
  // 지급 상태
  [PaymentStatus.PENDING]: '지급대기',
  [PaymentStatus.CALCULATED]: '계산완료',
  [PaymentStatus.APPROVED]: '승인완료',
  [PaymentStatus.PAID]: '지급완료',
  [PaymentStatus.CANCELLED]: '취소',
  
  // 급여 유형
  [SalaryType.MONTHLY]: '월급',
  [SalaryType.HOURLY]: '시급',
  [SalaryType.DAILY]: '일급',
  [SalaryType.BONUS]: '상여금',
  [SalaryType.SEVERANCE]: '퇴직금'
} as const