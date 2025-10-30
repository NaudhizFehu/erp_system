/**
 * 직원 등록/수정 폼 컴포넌트
 * 직원 정보를 입력하고 수정하는 폼입니다
 */

import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/contexts/AuthContext'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'
import { CalendarIcon, Loader2, Hash } from 'lucide-react'
import { Calendar } from '@/components/ui/calendar'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import { cn } from '@/lib/utils'
import { EmployeeNumberHelper } from './EmployeeNumberHelper'
import type { 
  Employee, 
  EmployeeCreateRequest, 
  EmployeeUpdateRequest,
  Company,
  Department,
  Position
} from '@/types/hr'
import { 
  Gender,
  EmploymentStatus,
  EmploymentType
} from '@/types/hr'
import { KOREAN_LABELS } from '@/types/hr'
import { useCheckEmployeeNumber, useCheckEmail } from '@/hooks/useEmployees'

// 폼 검증 스키마
const employeeFormSchema = z.object({
  employeeNumber: z.string()
    .min(1, '사번은 필수입니다')
    .max(20, '사번은 20자 이하여야 합니다')
    .regex(/^[A-Z0-9]+$/, '사번은 대문자와 숫자만 사용 가능합니다'),
  name: z.string()
    .min(1, '성명은 필수입니다')
    .max(50, '성명은 50자 이하여야 합니다'),
  nameEn: z.string()
    .max(100, '영문 성명은 100자 이하여야 합니다')
    .optional(),
  email: z.string()
    .min(1, '이메일은 필수입니다')
    .email('올바른 이메일 형식이어야 합니다')
    .max(100, '이메일은 100자 이하여야 합니다'),
  phone: z.string()
    .regex(/^\d{2,3}-\d{3,4}-\d{4}$/, '올바른 전화번호 형식이어야 합니다 (예: 02-1234-5678)')
    .optional()
    .or(z.literal('')),
  mobile: z.string()
    .min(1, '휴대폰번호는 필수입니다')
    .regex(/^010-\d{4}-\d{4}$/, '올바른 휴대폰번호 형식이어야 합니다 (예: 010-1234-5678)'),
  birthDate: z.date({
    required_error: '생년월일은 필수입니다'
  }),
  gender: z.nativeEnum(Gender, {
    required_error: '성별은 필수입니다'
  }),
  address: z.string().max(500, '주소는 500자 이하여야 합니다').optional(),
  addressDetail: z.string().max(200, '상세 주소는 200자 이하여야 합니다').optional(),
  postalCode: z.string()
    .regex(/^\d{5}$/, '올바른 우편번호 형식이어야 합니다 (5자리 숫자)')
    .optional()
    .or(z.literal('')),
  companyId: z.number().min(1, '소속 회사는 필수입니다'),
  department: z.number().min(1, '소속 부서는 필수입니다'),
  position: z.number().min(1, '직급은 필수입니다'),
  hireDate: z.date(),
  employmentStatus: z.nativeEnum(EmploymentStatus, {
    required_error: '근무 상태는 필수입니다'
  }),
  employmentType: z.nativeEnum(EmploymentType).optional(),
  baseSalary: z.number().min(1, '기본급은 1원 이상이어야 합니다'),
  bankName: z.string().min(1, '은행명은 필수입니다').max(50, '은행명은 50자 이하여야 합니다'),
  accountNumber: z.string().min(1, '계좌번호는 필수입니다').max(50, '계좌번호는 50자 이하여야 합니다'),
  accountHolder: z.string().min(1, '예금주명은 필수입니다').max(50, '예금주명은 50자 이하여야 합니다'),
  emergencyContact: z.string().max(20, '비상연락처는 20자 이하여야 합니다').optional(),
  emergencyRelation: z.string().max(20, '비상연락처 관계는 20자 이하여야 합니다').optional(),
  education: z.string().max(100, '학력은 100자 이하여야 합니다').optional(),
  major: z.string().max(100, '전공은 100자 이하여야 합니다').optional(),
  career: z.string().max(1000, '경력은 1000자 이하여야 합니다').optional(),
  skills: z.string().max(500, '기술 스택은 500자 이하여야 합니다').optional(),
  certifications: z.string().max(500, '자격증은 500자 이하여야 합니다').optional(),
  memo: z.string().optional(),
  profileImageUrl: z.string()
    .url('올바른 URL 형식이어야 합니다')
    .max(500, 'URL은 500자 이하여야 합니다')
    .optional()
    .or(z.literal(''))
})

type EmployeeFormData = z.infer<typeof employeeFormSchema>

interface EmployeeFormProps {
  employee?: Employee
  companies: Company[]
  departments: Department[]
  positions: Position[]
  onSubmit: (data: EmployeeCreateRequest | EmployeeUpdateRequest) => Promise<void>
  onCancel: () => void
  loading?: boolean
  mode?: 'create' | 'edit'
}

/**
 * 직원 폼 컴포넌트
 */
export function EmployeeForm({
  employee,
  companies,
  departments,
  positions,
  onSubmit,
  onCancel,
  loading = false,
  mode = 'create'
}: EmployeeFormProps) {
  const { user } = useAuth()
  const isEditMode = mode === 'edit'
  const [showEmployeeNumberHelper, setShowEmployeeNumberHelper] = useState(false)
  const [selectedCompanyFromHelper, setSelectedCompanyFromHelper] = useState<number | null>(null)
  const [isEmployeeNumberFromHelper, setIsEmployeeNumberFromHelper] = useState(false)
  
  // 사번 필드 읽기 전용 조건
  // ADMIN/MANAGER: 읽기 전용 (사번확인도우미만 사용 가능)
  // SUPER_ADMIN: 직접 입력 가능
  const isEmployeeNumberReadOnly = user?.role !== 'SUPER_ADMIN' && !isEditMode
  
  // 회사 필드 비활성화 조건
  // SUPER_ADMIN: 항상 변경 가능
  // ADMIN/MANAGER: 사번확인도우미 사용 시 변경 불가
  const isCompanyDisabled = user?.role !== 'SUPER_ADMIN' && isEmployeeNumberFromHelper

  // 폼 초기화
  const form = useForm<EmployeeFormData>({
    resolver: zodResolver(employeeFormSchema),
    defaultValues: {
      employeeNumber: employee?.employeeNumber || '',
      name: employee?.name || '',
      nameEn: employee?.nameEn || '',
      email: employee?.email || '',
      phone: employee?.phone || '',
      mobile: employee?.mobile || '',
      birthDate: employee?.birthDate ? new Date(employee.birthDate) : undefined,
      gender: employee?.gender,
      address: employee?.address || '',
      addressDetail: employee?.addressDetail || '',
      postalCode: employee?.postalCode || '',
      companyId: employee?.company.id || companies[0]?.id,
      department: employee?.department.id || departments[0]?.id,
      position: employee?.position.id || positions[0]?.id,
      hireDate: employee?.hireDate ? new Date(employee.hireDate) : new Date(),
      employmentStatus: employee?.employmentStatus || EmploymentStatus.ACTIVE,
      employmentType: employee?.employmentType || EmploymentType.FULL_TIME,
      baseSalary: employee?.baseSalary ?? 0,
      bankName: employee?.bankName || '',
      accountNumber: employee?.accountNumber || '',
      accountHolder: employee?.accountHolder || '',
      emergencyContact: employee?.emergencyContact || '',
      emergencyRelation: employee?.emergencyRelation || '',
      education: employee?.education || '',
      major: employee?.major || '',
      career: employee?.career || '',
      skills: employee?.skills || '',
      certifications: employee?.certifications || '',
      memo: employee?.memo || '',
      profileImageUrl: employee?.profileImageUrl || ''
    }
  })

  // 중복 확인
  const employeeNumberWatch = form.watch('employeeNumber')
  const emailWatch = form.watch('email')
  
  const { data: isEmployeeNumberExists } = useCheckEmployeeNumber(
    employeeNumberWatch || '', 
    isEditMode ? employee?.id : undefined
  )
  const { data: isEmailExists } = useCheckEmail(
    emailWatch || '',
    isEditMode ? employee?.id : undefined
  )

  // 폼 제출 처리
  const handleSubmit = async (data: EmployeeFormData) => {
    // 중복 검증
    if (isEmployeeNumberExists) {
      form.setError('employeeNumber', { message: '이미 사용 중인 사번입니다' })
      return
    }
    if (isEmailExists) {
      form.setError('email', { message: '이미 사용 중인 이메일입니다' })
      return
    }

    try {
      const { department, position, ...restData } = data
      
      const submitData = {
        ...restData,
        departmentId: department,
        positionId: position,
        birthDate: restData.birthDate?.toISOString().split('T')[0],
        hireDate: restData.hireDate.toISOString().split('T')[0],
        // 빈 문자열을 undefined로 변환
        phone: restData.phone || undefined,
        mobile: restData.mobile || undefined,
        address: restData.address || undefined,
        addressDetail: restData.addressDetail || undefined,
        postalCode: restData.postalCode || undefined,
        nameEn: restData.nameEn || undefined,
        bankName: restData.bankName || undefined,
        accountNumber: restData.accountNumber || undefined,
        accountHolder: restData.accountHolder || undefined,
        emergencyContact: restData.emergencyContact || undefined,
        emergencyRelation: restData.emergencyRelation || undefined,
        education: restData.education || undefined,
        major: restData.major || undefined,
        career: restData.career || undefined,
        skills: restData.skills || undefined,
        certifications: restData.certifications || undefined,
        memo: restData.memo || undefined,
        profileImageUrl: restData.profileImageUrl || undefined
      }

      await onSubmit(submitData)
    } catch (error) {
      console.error('폼 제출 오류:', error)
    }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
        {/* 기본 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>기본 정보</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* 사번 */}
            <FormField
              control={form.control}
              name="employeeNumber"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>사번 *</FormLabel>
                  <div className="flex gap-2">
                    <FormControl>
                      <Input 
                        placeholder="예: EMP001" 
                        {...field}
                        disabled={isEditMode}
                        readOnly={isEmployeeNumberReadOnly}
                        className="flex-1"
                        onChange={(e) => {
                          field.onChange(e)
                          // 사번이 수정되면 회사 고정 해제 (SUPER_ADMIN만)
                          if (isEmployeeNumberFromHelper && user?.role === 'SUPER_ADMIN') {
                            setIsEmployeeNumberFromHelper(false)
                            setSelectedCompanyFromHelper(null)
                          }
                        }}
                      />
                    </FormControl>
                    {!isEditMode && (
                      <Button
                        type="button"
                        variant="outline"
                        size="default"
                        onClick={() => setShowEmployeeNumberHelper(true)}
                        className="shrink-0"
                      >
                        <Hash className="h-4 w-4 mr-1" />
                        확인
                      </Button>
                    )}
                  </div>
                  {isEmployeeNumberReadOnly && !isEmployeeNumberFromHelper && (
                    <FormDescription className="text-amber-600">
                      사번은 사번확인도우미를 통해서만 입력할 수 있습니다.
                    </FormDescription>
                  )}
                  {isEmployeeNumberFromHelper && selectedCompanyFromHelper && (
                    <FormDescription className="text-blue-600">
                      {user?.role === 'SUPER_ADMIN' 
                        ? '사번확인도우미로 입력된 사번입니다. 회사가 자동 선택되었습니다.'
                        : '사번확인도우미로 입력된 사번입니다. 회사가 자동 선택되어 변경할 수 없습니다.'}
                    </FormDescription>
                  )}
                  {isEmployeeNumberExists && (
                    <FormDescription className="text-destructive">
                      이미 사용 중인 사번입니다
                    </FormDescription>
                  )}
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 이름 */}
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>성명 *</FormLabel>
                  <FormControl>
                    <Input placeholder="홍길동" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 영문 이름 */}
            <FormField
              control={form.control}
              name="nameEn"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>영문 성명</FormLabel>
                  <FormControl>
                    <Input placeholder="Hong Gil Dong" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 이메일 */}
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>이메일 *</FormLabel>
                  <FormControl>
                    <Input 
                      type="email" 
                      placeholder="hong@company.com" 
                      {...field} 
                    />
                  </FormControl>
                  {isEmailExists && (
                    <FormDescription className="text-destructive">
                      이미 사용 중인 이메일입니다
                    </FormDescription>
                  )}
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 전화번호 */}
            <FormField
              control={form.control}
              name="phone"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>전화번호</FormLabel>
                  <FormControl>
                    <Input placeholder="02-1234-5678" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 휴대폰 */}
            <FormField
              control={form.control}
              name="mobile"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>휴대폰 *</FormLabel>
                  <FormControl>
                    <Input placeholder="010-1234-5678" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 생년월일 */}
            <FormField
              control={form.control}
              name="birthDate"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>생년월일 *</FormLabel>
                  <Popover>
                    <PopoverTrigger asChild>
                      <FormControl>
                        <Button
                          variant="outline"
                          className={cn(
                            "w-full pl-3 text-left font-normal h-10",
                            !field.value && "text-muted-foreground"
                          )}
                        >
                          {field.value ? (
                            format(field.value, "yyyy년 MM월 dd일", { locale: ko })
                          ) : (
                            <span>날짜를 선택하세요</span>
                          )}
                          <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                        </Button>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        selected={field.value}
                        onSelect={field.onChange}
                        disabled={(date) =>
                          date > new Date() || date < new Date("1900-01-01")
                        }
                        initialFocus
                        fromYear={1950}
                        toYear={new Date().getFullYear()}
                      />
                    </PopoverContent>
                  </Popover>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 성별 */}
            <FormField
              control={form.control}
              name="gender"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>성별 *</FormLabel>
                  <Select 
                    onValueChange={field.onChange} 
                    value={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="성별을 선택하세요" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectItem value={Gender.MALE}>
                        {KOREAN_LABELS[Gender.MALE]}
                      </SelectItem>
                      <SelectItem value={Gender.FEMALE}>
                        {KOREAN_LABELS[Gender.FEMALE]}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        {/* 주소 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>주소 정보</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* 우편번호 */}
            <FormField
              control={form.control}
              name="postalCode"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>우편번호</FormLabel>
                  <FormControl>
                    <Input placeholder="12345" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div /> {/* 빈 공간 */}

            {/* 주소 */}
            <FormField
              control={form.control}
              name="address"
              render={({ field }) => (
                <FormItem className="md:col-span-2">
                  <FormLabel>주소</FormLabel>
                  <FormControl>
                    <Input placeholder="서울특별시 강남구 테헤란로 123" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 상세 주소 */}
            <FormField
              control={form.control}
              name="addressDetail"
              render={({ field }) => (
                <FormItem className="md:col-span-2">
                  <FormLabel>상세 주소</FormLabel>
                  <FormControl>
                    <Input placeholder="1층 101호" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        {/* 회사 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>회사 정보</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* 회사 */}
            <FormField
              control={form.control}
              name="companyId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>소속 회사 *</FormLabel>
                  <Select 
                    onValueChange={(value) => field.onChange(Number(value))}
                    value={field.value?.toString()}
                    disabled={isCompanyDisabled}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="회사를 선택하세요" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {companies.map((company) => (
                        <SelectItem key={company.id} value={company.id.toString()}>
                          {company.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {isCompanyDisabled && (
                    <FormDescription className="text-blue-600">
                      사번에 해당하는 회사가 자동 선택되어 변경할 수 없습니다.
                    </FormDescription>
                  )}
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 부서 */}
            <FormField
              control={form.control}
              name="department"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>소속 부서 *</FormLabel>
                  <Select 
                    onValueChange={(value) => field.onChange(Number(value))}
                    value={field.value?.toString()}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="부서를 선택하세요" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {departments
                        .filter((department, index, self) => 
                          self.findIndex(d => d.name === department.name) === index
                        )
                        .map((department) => (
                          <SelectItem key={department.id} value={department.id.toString()}>
                            {department.name}
                          </SelectItem>
                        ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 직급 */}
            <FormField
              control={form.control}
              name="position"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>직급 *</FormLabel>
                  <Select 
                    onValueChange={(value) => field.onChange(Number(value))}
                    value={field.value?.toString()}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="직급을 선택하세요" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {positions
                        .filter((position, index, self) => 
                          self.findIndex(p => p.name === position.name) === index
                        )
                        .map((position) => (
                          <SelectItem key={position.id} value={position.id.toString()}>
                            {position.name}
                          </SelectItem>
                        ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 입사일 */}
            <FormField
              control={form.control}
              name="hireDate"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>입사일 *</FormLabel>
                  <Popover>
                    <PopoverTrigger asChild>
                      <FormControl>
                        <Button
                          variant="outline"
                          className={cn(
                            "w-full pl-3 text-left font-normal h-10",
                            !field.value && "text-muted-foreground"
                          )}
                        >
                          {field.value ? (
                            format(field.value, "yyyy년 MM월 dd일", { locale: ko })
                          ) : (
                            <span>날짜를 선택하세요</span>
                          )}
                          <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                        </Button>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        selected={field.value}
                        onSelect={field.onChange}
                        disabled={(date) => date > new Date()}
                        initialFocus
                        fromYear={1990}
                        toYear={new Date().getFullYear()}
                      />
                    </PopoverContent>
                  </Popover>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 근무 상태 */}
            <FormField
              control={form.control}
              name="employmentStatus"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>근무 상태 *</FormLabel>
                  <Select 
                    onValueChange={field.onChange} 
                    value={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="근무 상태를 선택하세요" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {Object.values(EmploymentStatus).map((status) => (
                        <SelectItem key={status} value={status}>
                          {KOREAN_LABELS[status]}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 고용 형태 */}
            <FormField
              control={form.control}
              name="employmentType"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>고용 형태</FormLabel>
                  <Select 
                    onValueChange={field.onChange} 
                    value={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="고용 형태를 선택하세요" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {Object.values(EmploymentType).map((type) => (
                        <SelectItem key={type} value={type}>
                          {KOREAN_LABELS[type]}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        {/* 급여 및 계좌 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>급여 및 계좌 정보</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* 예금주명 */}
            <FormField
              control={form.control}
              name="accountHolder"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>예금주명 *</FormLabel>
                  <FormControl>
                    <Input placeholder="홍길동" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 기본급 */}
            <FormField
              control={form.control}
              name="baseSalary"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>기본급 *</FormLabel>
                  <FormControl>
                    <Input 
                      type="number" 
                      placeholder="3000000" 
                      min="0"
                      value={field.value ?? ''}
                      onChange={(e) => {
                        const value = e.target.value
                        if (value === '') {
                          field.onChange(0)
                        } else {
                          const numValue = Number(value)
                          field.onChange(numValue < 0 ? 0 : numValue)
                        }
                      }}
                      onBlur={field.onBlur}
                      name={field.name}
                      ref={field.ref}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 은행명 */}
            <FormField
              control={form.control}
              name="bankName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>은행명 *</FormLabel>
                  <FormControl>
                    <Input placeholder="국민은행" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 계좌번호 */}
            <FormField
              control={form.control}
              name="accountNumber"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>계좌번호 *</FormLabel>
                  <FormControl>
                    <Input placeholder="123-456-789012" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        {/* 비상연락처 */}
        <Card>
          <CardHeader>
            <CardTitle>비상연락처</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* 비상연락처 */}
            <FormField
              control={form.control}
              name="emergencyContact"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>비상연락처</FormLabel>
                  <FormControl>
                    <Input placeholder="010-9876-5432" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 관계 */}
            <FormField
              control={form.control}
              name="emergencyRelation"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>관계</FormLabel>
                  <FormControl>
                    <Input placeholder="배우자" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        {/* 학력 및 경력 */}
        <Card>
          <CardHeader>
            <CardTitle>학력 및 경력</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* 학력 */}
            <FormField
              control={form.control}
              name="education"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>학력</FormLabel>
                  <FormControl>
                    <Input placeholder="서울대학교 졸업" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 전공 */}
            <FormField
              control={form.control}
              name="major"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>전공</FormLabel>
                  <FormControl>
                    <Input placeholder="컴퓨터공학과" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 경력 */}
            <FormField
              control={form.control}
              name="career"
              render={({ field }) => (
                <FormItem className="md:col-span-2">
                  <FormLabel>경력</FormLabel>
                  <FormControl>
                    <Textarea 
                      placeholder="이전 직장 경력을 입력하세요..."
                      className="min-h-[80px]"
                      {...field} 
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 기술 스택 */}
            <FormField
              control={form.control}
              name="skills"
              render={({ field }) => (
                <FormItem className="md:col-span-2">
                  <FormLabel>기술 스택</FormLabel>
                  <FormControl>
                    <Input 
                      placeholder="Java, Spring Boot, React, TypeScript" 
                      {...field} 
                    />
                  </FormControl>
                  <FormDescription>
                    콤마(,)로 구분하여 입력하세요
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 자격증 */}
            <FormField
              control={form.control}
              name="certifications"
              render={({ field }) => (
                <FormItem className="md:col-span-2">
                  <FormLabel>자격증</FormLabel>
                  <FormControl>
                    <Input 
                      placeholder="정보처리기사, SQLD" 
                      {...field} 
                    />
                  </FormControl>
                  <FormDescription>
                    콤마(,)로 구분하여 입력하세요
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        {/* 기타 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>기타 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 프로필 이미지 URL */}
            <FormField
              control={form.control}
              name="profileImageUrl"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>프로필 이미지 URL</FormLabel>
                  <FormControl>
                    <Input 
                      type="url"
                      placeholder="https://example.com/profile.jpg" 
                      {...field} 
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* 메모 */}
            <FormField
              control={form.control}
              name="memo"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>메모</FormLabel>
                  <FormControl>
                    <Textarea 
                      placeholder="추가 정보나 특이사항을 입력하세요..."
                      className="min-h-[100px]"
                      {...field} 
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        {/* 버튼 */}
        <div className="flex justify-end space-x-2">
          <Button 
            type="button" 
            variant="outline" 
            onClick={onCancel}
            disabled={loading}
          >
            취소
          </Button>
          <Button type="submit" disabled={loading}>
            {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            {isEditMode ? '수정' : '등록'}
          </Button>
        </div>
      </form>

      {/* 사번 확인 도우미 */}
      <EmployeeNumberHelper 
        open={showEmployeeNumberHelper}
        onOpenChange={setShowEmployeeNumberHelper}
        onSelectEmployeeNumber={(employeeNumber, companyId) => {
          // 사번 입력
          form.setValue('employeeNumber', employeeNumber)
          
          // 회사 자동 선택
          form.setValue('companyId', companyId)
          
          // 상태 업데이트
          setSelectedCompanyFromHelper(companyId)
          setIsEmployeeNumberFromHelper(true)
        }}
      />
    </Form>
  )
}

