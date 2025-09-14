import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Save, ArrowLeft } from 'lucide-react'
import { EmployeeCreateDto } from '@/types/hr'

/**
 * 직원 등록/수정 폼 검증 스키마
 */
const employeeSchema = z.object({
  employeeNumber: z.string().min(1, '직원번호는 필수입니다'),
  name: z.string().min(1, '이름은 필수입니다'),
  email: z.string().email('올바른 이메일 형식이어야 합니다'),
  phone: z.string().optional(),
  hireDate: z.string().min(1, '입사일은 필수입니다'),
  departmentId: z.number().optional(),
  positionId: z.number().optional(),
  salary: z.number().min(0, '급여는 0 이상이어야 합니다').optional(),
  address: z.string().optional(),
  birthDate: z.string().optional(),
})

/**
 * 직원 등록/수정 폼 페이지
 * 새로운 직원을 등록하거나 기존 직원 정보를 수정합니다
 */
function EmployeeForm() {
  const navigate = useNavigate()
  const { id } = useParams()
  const isEdit = Boolean(id)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<EmployeeCreateDto>({
    resolver: zodResolver(employeeSchema),
    defaultValues: {
      employeeNumber: '',
      name: '',
      email: '',
      phone: '',
      hireDate: '',
      salary: 0,
      address: '',
      birthDate: '',
    },
  })

  const onSubmit = async (data: EmployeeCreateDto) => {
    try {
      console.log('폼 데이터:', data)
      // TODO: API 호출 로직 구현
      // if (isEdit) {
      //   await updateEmployee(id, data)
      // } else {
      //   await createEmployee(data)
      // }
      
      navigate('/hr/employees')
    } catch (error) {
      console.error('직원 저장 실패:', error)
    }
  }

  return (
    <div className="space-y-6">
      {/* 페이지 헤더 */}
      <div className="flex items-center space-x-4">
        <button
          onClick={() => navigate('/hr/employees')}
          className="btn btn-ghost p-2"
        >
          <ArrowLeft className="h-4 w-4" />
        </button>
        <div>
          <h1 className="text-3xl font-bold text-foreground">
            {isEdit ? '직원 정보 수정' : '직원 등록'}
          </h1>
          <p className="text-muted-foreground">
            {isEdit ? '직원 정보를 수정합니다' : '새로운 직원을 등록합니다'}
          </p>
        </div>
      </div>

      {/* 직원 정보 폼 */}
      <div className="rounded-lg border bg-card p-6">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          {/* 기본 정보 */}
          <div>
            <h3 className="text-lg font-semibold mb-4">기본 정보</h3>
            <div className="grid gap-4 md:grid-cols-2">
              <div className="form-group">
                <label className="form-label">직원번호 *</label>
                <input
                  {...register('employeeNumber')}
                  type="text"
                  className="form-input"
                  placeholder="예: EMP001"
                />
                {errors.employeeNumber && (
                  <p className="form-error">{errors.employeeNumber.message}</p>
                )}
              </div>

              <div className="form-group">
                <label className="form-label">이름 *</label>
                <input
                  {...register('name')}
                  type="text"
                  className="form-input"
                  placeholder="직원 이름을 입력하세요"
                />
                {errors.name && (
                  <p className="form-error">{errors.name.message}</p>
                )}
              </div>

              <div className="form-group">
                <label className="form-label">이메일 *</label>
                <input
                  {...register('email')}
                  type="email"
                  className="form-input"
                  placeholder="example@company.com"
                />
                {errors.email && (
                  <p className="form-error">{errors.email.message}</p>
                )}
              </div>

              <div className="form-group">
                <label className="form-label">전화번호</label>
                <input
                  {...register('phone')}
                  type="tel"
                  className="form-input"
                  placeholder="010-1234-5678"
                />
                {errors.phone && (
                  <p className="form-error">{errors.phone.message}</p>
                )}
              </div>

              <div className="form-group">
                <label className="form-label">입사일 *</label>
                <input
                  {...register('hireDate')}
                  type="date"
                  className="form-input"
                />
                {errors.hireDate && (
                  <p className="form-error">{errors.hireDate.message}</p>
                )}
              </div>

              <div className="form-group">
                <label className="form-label">생년월일</label>
                <input
                  {...register('birthDate')}
                  type="date"
                  className="form-input"
                />
                {errors.birthDate && (
                  <p className="form-error">{errors.birthDate.message}</p>
                )}
              </div>
            </div>
          </div>

          {/* 조직 정보 */}
          <div>
            <h3 className="text-lg font-semibold mb-4">조직 정보</h3>
            <div className="grid gap-4 md:grid-cols-2">
              <div className="form-group">
                <label className="form-label">부서</label>
                <select
                  {...register('departmentId', { valueAsNumber: true })}
                  className="form-input"
                >
                  <option value="">부서 선택</option>
                  <option value={1}>개발팀</option>
                  <option value={2}>마케팅팀</option>
                  <option value={3}>영업팀</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">직급</label>
                <select
                  {...register('positionId', { valueAsNumber: true })}
                  className="form-input"
                >
                  <option value="">직급 선택</option>
                  <option value={1}>인턴</option>
                  <option value={2}>주니어</option>
                  <option value={3}>시니어</option>
                  <option value={4}>팀장</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">급여</label>
                <input
                  {...register('salary', { valueAsNumber: true })}
                  type="number"
                  className="form-input"
                  placeholder="3000000"
                  min="0"
                />
                {errors.salary && (
                  <p className="form-error">{errors.salary.message}</p>
                )}
              </div>
            </div>
          </div>

          {/* 추가 정보 */}
          <div>
            <h3 className="text-lg font-semibold mb-4">추가 정보</h3>
            <div className="form-group">
              <label className="form-label">주소</label>
              <textarea
                {...register('address')}
                className="form-input min-h-[80px]"
                placeholder="주소를 입력하세요"
              />
            </div>
          </div>

          {/* 액션 버튼 */}
          <div className="flex justify-end space-x-4 pt-6 border-t">
            <button
              type="button"
              onClick={() => navigate('/hr/employees')}
              className="btn btn-outline"
            >
              취소
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="btn btn-primary"
            >
              <Save className="mr-2 h-4 w-4" />
              {isSubmitting ? '저장 중...' : isEdit ? '수정' : '등록'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default EmployeeForm





