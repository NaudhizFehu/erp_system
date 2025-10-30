/**
 * HR 모듈 Mock 데이터
 * 백엔드 서버 없이 프론트엔드 테스트를 위한 데이터
 */

import type { 
  Employee, 
  Position, 
  Department, 
  Company,
  StatisticsData,
  PageResponse
} from '@/types/hr'
import { Gender, EmploymentStatus, EmploymentType, PositionCategory, PositionType } from '@/types/hr'

// Mock 회사 데이터
export const mockCompanies: Company[] = [
  {
    id: 1,
    name: 'ABC 기업',
    businessNumber: '123-45-67890',
    ceoName: '김철수',
    address: '서울시 강남구 테헤란로 123',
    phone: '02-1234-5678',
    email: 'info@abc.com',
    website: 'https://abc.com',
    companyCode: 'ABC001',
    status: 'ACTIVE',
    createdAt: '2020-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  }
]

// Mock 부서 데이터
export const mockDepartments: Department[] = [
  {
    id: 1,
    departmentCode: 'DEV001',
    name: '개발팀',
    createdAt: '2020-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  },
  {
    id: 2,
    departmentCode: 'MKT001',
    name: '마케팅팀',
    createdAt: '2020-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  },
  {
    id: 3,
    departmentCode: 'HR001',
    name: '인사팀',
    createdAt: '2020-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  }
]

// Mock 직급 데이터
export const mockPositions: Position[] = [
  {
    id: 1,
    positionCode: 'DEV001',
    name: '시니어 개발자',
    positionLevel: 5,
    positionCategory: PositionCategory.SENIOR,
    positionType: PositionType.PERMANENT,
    company: mockCompanies[0],
    sortOrder: 1,
    isActive: true,
    employeeCount: 10,
    createdAt: '2020-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  },
  {
    id: 2,
    positionCode: 'DEV002',
    name: '주니어 개발자',
    positionLevel: 3,
    positionCategory: PositionCategory.JUNIOR,
    positionType: PositionType.PERMANENT,
    company: mockCompanies[0],
    sortOrder: 2,
    isActive: true,
    employeeCount: 8,
    createdAt: '2020-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  },
  {
    id: 3,
    positionCode: 'MKT001',
    name: '마케팅 매니저',
    positionLevel: 4,
    positionCategory: PositionCategory.MANAGEMENT,
    positionType: PositionType.PERMANENT,
    description: '마케팅 팀 매니저',
    company: mockCompanies[0],
    sortOrder: 1,
    employeeCount: 5,
    isActive: true,
    createdAt: '2020-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  }
]

// Mock 직원 데이터
export const mockEmployees: Employee[] = [
  {
    id: 1,
    employeeNumber: 'EMP001',
    name: '김철수',
    nameEn: 'Kim Chul Soo',
    email: 'kim@abc.com',
    phone: '02-1234-5678',
    mobile: '010-1234-5678',
    birthDate: '1990-05-15',
    gender: Gender.MALE,
    address: '서울시 강남구',
    addressDetail: '테헤란로 123',
    postalCode: '12345',
    company: mockCompanies[0],
    department: mockDepartments[0],
    position: mockPositions[0],
    hireDate: '2020-01-01',
    employmentStatus: EmploymentStatus.ACTIVE,
    employmentType: EmploymentType.FULL_TIME,
    bankName: '국민은행',
    accountNumber: '123456-78-901234',
    accountHolder: '김철수',
    emergencyContact: '010-9876-5432',
    emergencyRelation: '부',
    education: '대학교 졸업',
    major: '컴퓨터공학',
    career: '5년',
    skills: 'Java, Spring, React',
    certifications: '정보처리기사',
    memo: '우수한 개발자',
    profileImageUrl: '',
    terminationDate: undefined,
    yearsOfService: 2,
    age: 30,
    createdAt: '2020-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  },
  {
    id: 2,
    employeeNumber: 'EMP002',
    name: '이영희',
    nameEn: 'Lee Young Hee',
    email: 'lee@abc.com',
    phone: '02-2345-6789',
    mobile: '010-2345-6789',
    birthDate: '1992-08-20',
    gender: Gender.FEMALE,
    address: '서울시 서초구',
    addressDetail: '서초대로 456',
    postalCode: '23456',
    company: mockCompanies[0],
    department: mockDepartments[1],
    position: mockPositions[2],
    hireDate: '2021-03-01',
    employmentStatus: EmploymentStatus.ACTIVE,
    employmentType: EmploymentType.FULL_TIME,
    bankName: '신한은행',
    accountNumber: '234567-89-012345',
    accountHolder: '이영희',
    emergencyContact: '010-8765-4321',
    emergencyRelation: '어머니',
    education: '대학교 졸업',
    major: '마케팅',
    career: '3년',
    skills: '마케팅, 광고',
    certifications: '마케팅 자격증',
    memo: '창의적인 마케터',
    profileImageUrl: '',
    terminationDate: undefined,
    yearsOfService: 1,
    age: 28,
    createdAt: '2021-03-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  },
  {
    id: 3,
    employeeNumber: 'EMP003',
    name: '박민수',
    nameEn: 'Park Min Soo',
    email: 'park@abc.com',
    phone: '02-3456-7890',
    mobile: '010-3456-7890',
    birthDate: '1995-12-10',
    gender: Gender.MALE,
    address: '서울시 송파구',
    addressDetail: '송파대로 789',
    postalCode: '34567',
    company: mockCompanies[0],
    department: mockDepartments[2],
    position: mockPositions[1],
    hireDate: '2022-06-01',
    employmentStatus: EmploymentStatus.ACTIVE,
    employmentType: EmploymentType.FULL_TIME,
    bankName: '우리은행',
    accountNumber: '345678-90-123456',
    accountHolder: '박민수',
    emergencyContact: '010-7654-3210',
    emergencyRelation: '형',
    education: '대학교 졸업',
    major: '경영학',
    career: '2년',
    skills: '인사관리, HR',
    certifications: '인사관리사',
    memo: '성실한 인사담당자',
    profileImageUrl: '',
    terminationDate: undefined,
    yearsOfService: 3,
    age: 32,
    createdAt: '2022-06-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  }
]

// Mock 통계 데이터
export const mockPositionStats: StatisticsData[] = [
  { label: '시니어 개발자', count: 1 },
  { label: '주니어 개발자', count: 1 },
  { label: '마케팅 매니저', count: 1 }
]

export const mockDepartmentStats: StatisticsData[] = [
  { label: '개발팀', count: 1 },
  { label: '마케팅팀', count: 1 },
  { label: '인사팀', count: 1 }
]

export const mockGenderStats: StatisticsData[] = [
  { label: '남성', count: 2 },
  { label: '여성', count: 1 }
]

export const mockAgeGroupStats: StatisticsData[] = [
  { label: '20대', count: 1 },
  { label: '30대', count: 2 }
]

// Mock 페이지네이션 응답 생성 함수
export function createMockPageResponse<T>(data: T[], page: number = 0, size: number = 20): PageResponse<T> {
  const startIndex = page * size
  const endIndex = startIndex + size
  const content = data.slice(startIndex, endIndex)
  
  return {
    content,
    pageable: {
      pageNumber: page,
      pageSize: size,
      sort: {
        sorted: false,
        unsorted: true,
        empty: true
      },
      offset: startIndex,
      paged: true,
      unpaged: false
    },
    totalElements: data.length,
    totalPages: Math.ceil(data.length / size),
    last: endIndex >= data.length,
    first: page === 0,
    size,
    number: page,
    numberOfElements: content.length,
    empty: content.length === 0,
    sort: {
      sorted: false,
      unsorted: true,
      empty: true
    }
  }
}



