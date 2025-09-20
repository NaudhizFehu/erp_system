import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Save, Eye, EyeOff, User, Mail, Phone, Building } from 'lucide-react'
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
  const [success, setSuccess] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [showPassword, setShowPassword] = useState(false)
  const [departments, setDepartments] = useState<{ id: number; name: string }[]>([])
  const [positions, setPositions] = useState<{ id: number; name: string }[]>([])
  
  const [formData, setFormData] = useState({
    username: '',
    fullName: '',
    email: '',
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
      setFormData({
        username: user.username || '',
        fullName: user.fullName || '',
        email: user.email || '',
        phoneNumber: user.phoneNumber || '',
        departmentId: user.department?.id?.toString() || '',
        departmentName: user.department?.name || '',
        positionId: '', // TODO: position 정보가 user에 없으므로 추후 추가
        positionName: '',
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
        
      } catch (error) {
        console.error('드롭다운 데이터 로드 실패:', error)
        setError('부서/직급 목록을 불러오는 중 오류가 발생했습니다.')
      } finally {
        setLoading(false)
      }
    }

    loadDropdownData()
  }, [isAdmin])

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
    if (!formData.fullName.trim()) {
      setError('이름은 필수입니다.')
      return false
    }
    
    if (!formData.email.trim()) {
      setError('이메일은 필수입니다.')
      return false
    }
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(formData.email)) {
      setError('올바른 이메일 형식을 입력해주세요.')
      return false
    }

    // 비밀번호 변경 시 검증
    if (formData.newPassword || formData.confirmPassword) {
      if (!formData.currentPassword) {
        setError('현재 비밀번호를 입력해주세요.')
        return false
      }
      
      if (formData.newPassword.length < 6) {
        setError('새 비밀번호는 6자 이상이어야 합니다.')
        return false
      }
      
      if (formData.newPassword !== formData.confirmPassword) {
        setError('새 비밀번호가 일치하지 않습니다.')
        return false
      }
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
      setError(null)
      setSuccess(null)

      // 프로필 정보 업데이트
      const profileData: UpdateUserProfileRequest = {
        fullName: formData.fullName,
        email: formData.email,
        phoneNumber: formData.phoneNumber,
        department: formData.departmentName,
        position: formData.positionName
      }

      const updatedUser = await userService.updateProfile(profileData)
      updateUser(updatedUser)
      
      // 비밀번호 변경이 요청된 경우
      if (formData.newPassword && formData.currentPassword) {
        await userService.changePassword({
          currentPassword: formData.currentPassword,
          newPassword: formData.newPassword
        })
      }
      
      setSuccess('사용자 정보가 성공적으로 저장되었습니다!')
      
      // 비밀번호 필드 초기화
      setFormData(prev => ({
        ...prev,
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }))
      
    } catch (error) {
      console.error('사용자 정보 저장 실패:', error)
      setError('사용자 정보 저장에 실패했습니다.')
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
                onChange={(e) => updateFormData('fullName', e.target.value)}
                placeholder="이름을 입력하세요"
              />
            </div>

            <div>
              <Label htmlFor="email">이메일 *</Label>
              <Input
                id="email"
                type="email"
                value={formData.email}
                onChange={(e) => updateFormData('email', e.target.value)}
                placeholder="이메일을 입력하세요"
              />
            </div>

            <div>
              <Label htmlFor="phoneNumber">전화번호</Label>
              <Input
                id="phoneNumber"
                value={formData.phoneNumber}
                onChange={(e) => updateFormData('phoneNumber', e.target.value)}
                placeholder="전화번호를 입력하세요"
              />
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
              <Label htmlFor="department">부서</Label>
              {isAdmin ? (
                <Select value={formData.departmentId} onValueChange={handleDepartmentSelect}>
                  <SelectTrigger>
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
                  className="bg-muted"
                />
              )}
            </div>

            <div>
              <Label htmlFor="position">직급</Label>
              {isAdmin ? (
                <Select value={formData.positionId} onValueChange={handlePositionSelect}>
                  <SelectTrigger>
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
                  className="bg-muted"
                />
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
                onChange={(e) => updateFormData('currentPassword', e.target.value)}
                placeholder="현재 비밀번호를 입력하세요"
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
          </div>

          <div>
            <Label htmlFor="newPassword">새 비밀번호</Label>
            <Input
              id="newPassword"
              type={showPassword ? "text" : "password"}
              value={formData.newPassword}
              onChange={(e) => updateFormData('newPassword', e.target.value)}
              placeholder="새 비밀번호를 입력하세요"
            />
          </div>

          <div>
            <Label htmlFor="confirmPassword">새 비밀번호 확인</Label>
            <Input
              id="confirmPassword"
              type={showPassword ? "text" : "password"}
              value={formData.confirmPassword}
              onChange={(e) => updateFormData('confirmPassword', e.target.value)}
              placeholder="새 비밀번호를 다시 입력하세요"
            />
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

      {/* 결과 메시지 */}
      {success && (
        <Card className="mt-6 border-green-200 bg-green-50">
          <CardContent className="p-4">
            <p className="text-green-800">{success}</p>
          </CardContent>
        </Card>
      )}

      {error && (
        <Card className="mt-6 border-red-200 bg-red-50">
          <CardContent className="p-4">
            <p className="text-red-800">{error}</p>
          </CardContent>
        </Card>
      )}
    </div>
  )
}

export { UserProfilePage }
