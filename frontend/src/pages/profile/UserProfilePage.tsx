import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Save, Eye, EyeOff, User, Mail, Phone, Building } from 'lucide-react'
import toast from 'react-hot-toast'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Separator } from '@/components/ui/separator'
import { useAuth } from '@/contexts/AuthContext'
import { userService, type UpdateUserProfileRequest } from '@/services/userService'
import { departmentService } from '@/services/departmentService'
import { positionService } from '@/services/positionService'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'

/**
 * 사용자 프로필 페이지
 * 사용자 정보 조회 및 수정 기능을 제공합니다
 */
function UserProfilePage() {
  const navigate = useNavigate()
  const { user, updateUser } = useAuth()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [departments, setDepartments] = useState<{ id: number; name: string }[]>([])
  const [positions, setPositions] = useState<{ id: number; name: string }[]>([])
  
  // 필드별 에러 상태 관리
  const [fieldErrors, setFieldErrors] = useState<{
    fullName?: string
    email?: string
    phone?: string
    phoneNumber?: string
    currentPassword?: string
    newPassword?: string
    confirmPassword?: string
  }>({})
  
  const [formData, setFormData] = useState({
    username: '',
    fullName: '',
    email: '',
    phone: '',
    phoneNumber: '',
    departmentId: '',
    departmentName: '',
    positionId: '',
    positionName: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })

  // 관리자 권한 확인
  const isAdmin = user?.role === 'ADMIN' || user?.role === 'SUPER_ADMIN'

  /**
   * 폼 데이터 초기화
   */
  useEffect(() => {
    if (user) {
      console.log('UserProfilePage useEffect에서 user 정보:', user)
      console.log('UserProfilePage useEffect에서 user 정보 상세:', JSON.stringify(user, null, 2))
      console.log('UserProfilePage useEffect department 필드:', user.department)
      console.log('UserProfilePage useEffect department?.name:', user.department?.name)
      console.log('UserProfilePage useEffect position 필드:', user.position)
      setFormData({
        username: user.username || '',
        fullName: user.fullName || '',
        email: user.email || '',
        phone: user.phone || '',
        phoneNumber: user.phoneNumber || '',
        departmentId: user.department?.id?.toString() || '',
        departmentName: user.department?.name || '',
        positionId: '', // position은 문자열로 저장되므로 ID는 별도 관리 필요
        positionName: user.position || '', // user.position 필드 사용
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      })
    }
  }, [user])

  /**
   * 부서 및 직급 데이터 로드 (관리자만)
   */
  useEffect(() => {
    const loadDropdownData = async () => {
      if (!isAdmin) return

      try {
        setLoading(true)
        
        // 부서 목록 로드
        const deptData = await departmentService.getAllDepartments()
        setDepartments(deptData)
        
        // 직급 목록 로드
        const posData = await positionService.getAllPositions()
        setPositions(posData)
        
        // 현재 사용자의 부서/직급에 맞는 ID 설정
        if (user) {
          setFormData(prev => {
            const selectedDept = deptData.find(dept => dept.name === user.department?.name)
            const selectedPos = posData.find(pos => pos.name === user.position)
            
            console.log('드롭다운 로딩 후 ID 설정:')
            console.log('사용자 부서명:', user.department?.name)
            console.log('선택된 부서:', selectedDept)
            console.log('사용자 직급:', user.position)
            console.log('선택된 직급:', selectedPos)
            
            return {
              ...prev,
              departmentId: selectedDept ? selectedDept.id.toString() : prev.departmentId,
              positionId: selectedPos ? selectedPos.id.toString() : prev.positionId
            }
          })
        }
        
      } catch (error) {
        console.error('드롭다운 데이터 로드 실패:', error)
        toast.error('부서/직급 목록을 불러오는 중 오류가 발생했습니다.')
      } finally {
        setLoading(false)
      }
    }

    loadDropdownData()
  }, [isAdmin, user])

  /**
   * 폼 데이터 업데이트
   */
  const updateFormData = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }))
  }

  /**
   * 부서 선택 핸들러
   */
  const handleDepartmentSelect = (departmentId: string) => {
    const selectedDept = departments.find(dept => dept.id.toString() === departmentId)
    setFormData(prev => ({
      ...prev,
      departmentId,
      departmentName: selectedDept?.name || ''
    }))
  }

  /**
   * 직급 선택 핸들러
   */
  const handlePositionSelect = (positionId: string) => {
    const selectedPos = positions.find(pos => pos.id.toString() === positionId)
    setFormData(prev => ({
      ...prev,
      positionId,
      positionName: selectedPos?.name || ''
    }))
  }

  /**
   * 폼 유효성 검사
   */
  const validateForm = () => {
    const errors: typeof fieldErrors = {}
    let hasErrors = false

    // 이름 검증
    if (!formData.fullName.trim()) {
      errors.fullName = '이름은 필수입니다.'
      hasErrors = true
    }
    
    // 이메일 검증
    if (!formData.email.trim()) {
      errors.email = '이메일은 필수입니다.'
      hasErrors = true
    } else {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(formData.email)) {
        errors.email = '올바른 이메일 형식을 입력해주세요.'
        hasErrors = true
      }
    }

    // 부서 검증 (필수)
    if (!formData.departmentName?.trim()) {
      errors.departmentName = '부서는 필수입니다.'
      hasErrors = true
    }
    
    // 직급 검증 (필수)
    if (!formData.positionName?.trim()) {
      errors.positionName = '직급은 필수입니다.'
      hasErrors = true
    }

    // 전화번호 검증 (선택사항이지만 형식이 있으면 검증)
    if (formData.phone && formData.phone.trim()) {
      const phoneRegex = /^(01[016789]-?\d{3,4}-?\d{4}|0[2-6]\d?-?\d{3,4}-?\d{4}|\d{4}-?\d{4}|\d{10,11}|\d{8})$/
      if (!phoneRegex.test(formData.phone.trim())) {
        errors.phone = '올바른 유선전화번호 형식을 입력해주세요.'
        hasErrors = true
      }
    }

    if (formData.phoneNumber && formData.phoneNumber.trim()) {
      const phoneRegex = /^(01[016789]-?\d{3,4}-?\d{4}|0[2-6]\d?-?\d{3,4}-?\d{4}|\d{4}-?\d{4}|\d{10,11}|\d{8})$/
      if (!phoneRegex.test(formData.phoneNumber.trim())) {
        errors.phoneNumber = '올바른 휴대폰번호 형식을 입력해주세요.'
        hasErrors = true
      }
    }

    // 비밀번호 변경 시 검증
    if (formData.newPassword || formData.confirmPassword) {
      if (!formData.currentPassword) {
        errors.currentPassword = '현재 비밀번호를 입력해주세요.'
        hasErrors = true
      }
      
      if (formData.newPassword && formData.newPassword.length < 6) {
        errors.newPassword = '새 비밀번호는 6자 이상이어야 합니다.'
        hasErrors = true
      }
      
      if (formData.newPassword && formData.confirmPassword && formData.newPassword !== formData.confirmPassword) {
        errors.confirmPassword = '새 비밀번호가 일치하지 않습니다.'
        hasErrors = true
      }
    }

    setFieldErrors(errors)
    
    if (hasErrors) {
      toast.error('입력 정보를 확인해주세요.')
      return false
    }

    return true
  }

  /**
   * 사용자 정보 저장
   */
  const handleSave = async () => {
    if (!validateForm()) return

    try {
      setSaving(true)
      setFieldErrors({}) // 필드별 에러 상태 초기화

      // 프로필 정보 업데이트 (부서/직급은 필수값)
      const profileData: UpdateUserProfileRequest = {
        fullName: formData.fullName?.trim() || '',
        email: formData.email?.trim() || '',
        phone: formData.phone?.trim() || undefined,
        phoneNumber: formData.phoneNumber?.trim() || undefined,
        department: formData.departmentName?.trim() || '', // 필수값으로 처리
        position: formData.positionName?.trim() || '' // 필수값으로 처리
      }

      // 빈 문자열인 선택적 필드들을 제거 (부서/직급은 필수값이므로 제거하지 않음)
      Object.keys(profileData).forEach(key => {
        const value = profileData[key as keyof UpdateUserProfileRequest]
        if ((key === 'phone' || key === 'phoneNumber') && (value === '' || value === null)) {
          delete profileData[key as keyof UpdateUserProfileRequest]
        }
      })

      const updatedUser = await userService.updateProfile(profileData)
      console.log('업데이트된 사용자 정보:', updatedUser)
      console.log('업데이트된 사용자 정보 상세:', JSON.stringify(updatedUser, null, 2))
      console.log('department 필드:', updatedUser.department)
      console.log('position 필드:', updatedUser.position)
      
      // 폼 데이터를 업데이트된 사용자 정보로 동기화
      // 부서와 직급의 ID도 찾아서 설정해야 함
      const selectedDepartment = departments.find(dept => dept.name === updatedUser.department)
      const selectedPosition = positions.find(pos => pos.name === updatedUser.position)
      
      const newFormData = {
        ...formData,
        fullName: updatedUser.fullName || '',
        email: updatedUser.email || '',
        phone: updatedUser.phone || '',
        phoneNumber: updatedUser.phoneNumber || '',
        departmentId: selectedDepartment ? selectedDepartment.id.toString() : '',
        departmentName: updatedUser.department || '',
        positionId: selectedPosition ? selectedPosition.id.toString() : '',
        positionName: updatedUser.position || '',
        // 비밀번호 필드는 초기화
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      setFormData(newFormData)
      
      // 전역 상태 업데이트 - department 객체를 올바르게 구성
      const updatedUserWithDepartment = {
        ...updatedUser,
        department: selectedDepartment ? {
          id: selectedDepartment.id,
          name: selectedDepartment.name,
          departmentCode: selectedDepartment.departmentCode || ''
        } : null
      }
      updateUser(updatedUserWithDepartment)
      
      // 비밀번호 변경이 요청된 경우
      if (formData.newPassword && formData.currentPassword) {
        await userService.changePassword({
          currentPassword: formData.currentPassword,
          newPassword: formData.newPassword
        })
      }
      
      toast.success('사용자 정보가 성공적으로 저장되었습니다!')
      
    } catch (error) {
      console.error('사용자 정보 저장 실패:', error)
      toast.error('사용자 정보 저장에 실패했습니다.')
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <LoadingSpinner />
      </div>
    )
  }

  if (!user) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Card>
          <CardContent className="p-6">
            <p className="text-center text-muted-foreground">사용자 정보를 불러올 수 없습니다.</p>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8">
      {/* 헤더 */}
      <div className="flex items-center gap-4 mb-6">
        <Button
          variant="ghost"
          onClick={() => navigate(-1)}
          className="flex items-center gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          뒤로가기
        </Button>
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2">
            <User className="h-6 w-6" />
            사용자 정보
          </h1>
          <p className="text-muted-foreground">
            개인 정보를 확인하고 수정할 수 있습니다
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* 기본 정보 */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <User className="h-5 w-5" />
              기본 정보
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <Label htmlFor="username">사용자명</Label>
              <Input
                id="username"
                value={formData.username}
                disabled
                className="bg-muted"
              />
              <p className="text-xs text-muted-foreground mt-1">
                사용자명은 변경할 수 없습니다
              </p>
            </div>

            <div>
              <Label htmlFor="fullName">이름 *</Label>
              <Input
                id="fullName"
                value={formData.fullName}
                onChange={(e) => {
                  updateFormData('fullName', e.target.value)
                  // 에러 상태 초기화
                  if (fieldErrors.fullName) {
                    setFieldErrors(prev => ({ ...prev, fullName: undefined }))
                  }
                }}
                placeholder="이름을 입력하세요"
                className={fieldErrors.fullName ? "border-red-500 focus-visible:ring-red-500" : ""}
              />
              {fieldErrors.fullName && (
                <p className="text-sm text-red-600 mt-1">{fieldErrors.fullName}</p>
              )}
            </div>

            <div>
              <Label htmlFor="email">이메일 *</Label>
              <Input
                id="email"
                type="email"
                value={formData.email}
                onChange={(e) => {
                  updateFormData('email', e.target.value)
                  // 에러 상태 초기화
                  if (fieldErrors.email) {
                    setFieldErrors(prev => ({ ...prev, email: undefined }))
                  }
                }}
                placeholder="이메일을 입력하세요"
                className={fieldErrors.email ? "border-red-500 focus-visible:ring-red-500" : ""}
              />
              {fieldErrors.email && (
                <p className="text-sm text-red-600 mt-1">{fieldErrors.email}</p>
              )}
            </div>

            <div>
              <Label htmlFor="phone">유선전화</Label>
              <Input
                id="phone"
                value={formData.phone}
                onChange={(e) => {
                  updateFormData('phone', e.target.value)
                  // 에러 상태 초기화
                  if (fieldErrors.phone) {
                    setFieldErrors(prev => ({ ...prev, phone: undefined }))
                  }
                }}
                placeholder="유선전화번호를 입력하세요 (예: 02-123-4567)"
                className={fieldErrors.phone ? "border-red-500 focus-visible:ring-red-500" : ""}
              />
              {fieldErrors.phone && (
                <p className="text-sm text-red-600 mt-1">{fieldErrors.phone}</p>
              )}
            </div>

            <div>
              <Label htmlFor="phoneNumber">휴대폰</Label>
              <Input
                id="phoneNumber"
                value={formData.phoneNumber}
                onChange={(e) => {
                  updateFormData('phoneNumber', e.target.value)
                  // 에러 상태 초기화
                  if (fieldErrors.phoneNumber) {
                    setFieldErrors(prev => ({ ...prev, phoneNumber: undefined }))
                  }
                }}
                placeholder="휴대폰번호를 입력하세요 (예: 010-1234-5678)"
                className={fieldErrors.phoneNumber ? "border-red-500 focus-visible:ring-red-500" : ""}
              />
              {fieldErrors.phoneNumber && (
                <p className="text-sm text-red-600 mt-1">{fieldErrors.phoneNumber}</p>
              )}
            </div>
          </CardContent>
        </Card>

        {/* 직무 정보 */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Building className="h-5 w-5" />
              직무 정보
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <Label htmlFor="department">
                부서 <span className="text-red-500">*</span>
              </Label>
              {isAdmin ? (
                <Select value={formData.departmentId} onValueChange={handleDepartmentSelect}>
                  <SelectTrigger className={fieldErrors.departmentName ? 'border-red-500' : ''}>
                    <SelectValue placeholder="부서를 선택하세요" />
                  </SelectTrigger>
                  <SelectContent>
                    {departments.map((dept) => (
                      <SelectItem key={dept.id} value={dept.id.toString()}>
                        {dept.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              ) : (
                <Input
                  value={formData.departmentName}
                  disabled
                  className={`bg-muted ${fieldErrors.departmentName ? 'border-red-500' : ''}`}
                />
              )}
              {fieldErrors.departmentName && (
                <p className="text-sm text-red-500">{fieldErrors.departmentName}</p>
              )}
            </div>

            <div>
              <Label htmlFor="position">
                직급 <span className="text-red-500">*</span>
              </Label>
              {isAdmin ? (
                <Select value={formData.positionId} onValueChange={handlePositionSelect}>
                  <SelectTrigger className={fieldErrors.positionName ? 'border-red-500' : ''}>
                    <SelectValue placeholder="직급을 선택하세요" />
                  </SelectTrigger>
                  <SelectContent>
                    {positions.map((pos) => (
                      <SelectItem key={pos.id} value={pos.id.toString()}>
                        {pos.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              ) : (
                <Input
                  value={formData.positionName}
                  disabled
                  className={`bg-muted ${fieldErrors.positionName ? 'border-red-500' : ''}`}
                />
              )}
              {fieldErrors.positionName && (
                <p className="text-sm text-red-500">{fieldErrors.positionName}</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 비밀번호 변경 */}
      <Card className="mt-6">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Eye className="h-5 w-5" />
            비밀번호 변경
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <Label htmlFor="currentPassword">현재 비밀번호</Label>
            <div className="relative">
              <Input
                id="currentPassword"
                type={showPassword ? "text" : "password"}
                value={formData.currentPassword}
                onChange={(e) => {
                  updateFormData('currentPassword', e.target.value)
                  // 에러 상태 초기화
                  if (fieldErrors.currentPassword) {
                    setFieldErrors(prev => ({ ...prev, currentPassword: undefined }))
                  }
                }}
                placeholder="현재 비밀번호를 입력하세요"
                className={fieldErrors.currentPassword ? "border-red-500 focus-visible:ring-red-500" : ""}
              />
              <Button
                type="button"
                variant="ghost"
                size="sm"
                className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? (
                  <EyeOff className="h-4 w-4" />
                ) : (
                  <Eye className="h-4 w-4" />
                )}
              </Button>
            </div>
            {fieldErrors.currentPassword && (
              <p className="text-sm text-red-600 mt-1">{fieldErrors.currentPassword}</p>
            )}
          </div>

          <div>
            <Label htmlFor="newPassword">새 비밀번호</Label>
            <Input
              id="newPassword"
              type={showPassword ? "text" : "password"}
              value={formData.newPassword}
              onChange={(e) => {
                updateFormData('newPassword', e.target.value)
                // 에러 상태 초기화
                if (fieldErrors.newPassword) {
                  setFieldErrors(prev => ({ ...prev, newPassword: undefined }))
                }
              }}
              placeholder="새 비밀번호를 입력하세요"
              className={fieldErrors.newPassword ? "border-red-500 focus-visible:ring-red-500" : ""}
            />
            {fieldErrors.newPassword && (
              <p className="text-sm text-red-600 mt-1">{fieldErrors.newPassword}</p>
            )}
          </div>

          <div>
            <Label htmlFor="confirmPassword">새 비밀번호 확인</Label>
            <Input
              id="confirmPassword"
              type={showPassword ? "text" : "password"}
              value={formData.confirmPassword}
              onChange={(e) => {
                updateFormData('confirmPassword', e.target.value)
                // 에러 상태 초기화
                if (fieldErrors.confirmPassword) {
                  setFieldErrors(prev => ({ ...prev, confirmPassword: undefined }))
                }
              }}
              placeholder="새 비밀번호를 다시 입력하세요"
              className={fieldErrors.confirmPassword ? "border-red-500 focus-visible:ring-red-500" : ""}
            />
            {fieldErrors.confirmPassword && (
              <p className="text-sm text-red-600 mt-1">{fieldErrors.confirmPassword}</p>
            )}
          </div>

          <p className="text-xs text-muted-foreground">
            비밀번호를 변경하지 않으려면 비밀번호 필드를 비워두세요
          </p>
        </CardContent>
      </Card>

      {/* 저장 버튼 */}
      <div className="flex justify-end mt-6">
        <Button 
          onClick={handleSave} 
          disabled={saving}
          className="flex items-center gap-2"
        >
          {saving ? (
            <>
              <LoadingSpinner className="h-4 w-4" />
              저장 중...
            </>
          ) : (
            <>
              <Save className="h-4 w-4" />
              저장
            </>
          )}
        </Button>
      </div>

    </div>
  )
}

export { UserProfilePage }
