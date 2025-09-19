import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Bell, Send, RefreshCw } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { notificationService } from '@/services/notificationService'
import { useNotifications } from '@/contexts/NotificationContext'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'

/**
 * 알림 테스트 페이지
 * 개발/테스트용 알림을 생성할 수 있습니다
 */
function NotificationTestPage() {
  const navigate = useNavigate()
  const { refreshNotifications } = useNotifications()
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  
  const [formData, setFormData] = useState({
    title: '',
    message: '',
    type: 'INFO' as 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS',
    actionUrl: ''
  })

  /**
   * 폼 데이터 업데이트
   */
  const updateFormData = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }))
  }

  /**
   * 테스트 알림 생성
   */
  const createTestNotification = async () => {
    if (!formData.title.trim() || !formData.message.trim()) {
      setError('제목과 메시지는 필수입니다.')
      return
    }

    try {
      setLoading(true)
      setError(null)
      setSuccess(null)

      await notificationService.createTestNotification({
        title: formData.title,
        message: formData.message,
        type: formData.type,
        actionUrl: formData.actionUrl || undefined
      })

      setSuccess('테스트 알림이 성공적으로 생성되었습니다!')
      
      // 전역 알림 상태 갱신
      await refreshNotifications()
      
      // 폼 초기화
      setFormData({
        title: '',
        message: '',
        type: 'INFO',
        actionUrl: ''
      })
    } catch (error) {
      console.error('테스트 알림 생성 실패:', error)
      setError('테스트 알림 생성에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  /**
   * 빠른 테스트 알림 생성
   */
  const createQuickTestNotification = async (type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS') => {
    const testData = {
      INFO: {
        title: '정보 알림 테스트',
        message: '이것은 정보 타입의 테스트 알림입니다. 시스템이 정상적으로 작동하고 있습니다.',
        actionUrl: '/dashboard'
      },
      WARNING: {
        title: '경고 알림 테스트',
        message: '이것은 경고 타입의 테스트 알림입니다. 주의가 필요한 상황입니다.',
        actionUrl: '/inventory'
      },
      ERROR: {
        title: '오류 알림 테스트',
        message: '이것은 오류 타입의 테스트 알림입니다. 즉시 확인이 필요합니다.',
        actionUrl: '/sales'
      },
      SUCCESS: {
        title: '성공 알림 테스트',
        message: '이것은 성공 타입의 테스트 알림입니다. 작업이 성공적으로 완료되었습니다.',
        actionUrl: '/hr'
      }
    }

    const data = testData[type]
    
    try {
      setLoading(true)
      setError(null)
      setSuccess(null)

      await notificationService.createTestNotification({
        title: data.title,
        message: data.message,
        type: type,
        actionUrl: data.actionUrl
      })

      setSuccess(`${type} 타입 테스트 알림이 성공적으로 생성되었습니다!`)
      
      // 전역 알림 상태 갱신
      await refreshNotifications()
    } catch (error) {
      console.error('빠른 테스트 알림 생성 실패:', error)
      setError('빠른 테스트 알림 생성에 실패했습니다.')
    } finally {
      setLoading(false)
    }
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
            <Bell className="h-6 w-6" />
            알림 테스트
          </h1>
          <p className="text-muted-foreground">
            개발/테스트용 알림을 생성할 수 있습니다
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* 커스텀 알림 생성 */}
        <Card>
          <CardHeader>
            <CardTitle>커스텀 알림 생성</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <Label htmlFor="title">제목</Label>
              <Input
                id="title"
                value={formData.title}
                onChange={(e) => updateFormData('title', e.target.value)}
                placeholder="알림 제목을 입력하세요"
              />
            </div>

            <div>
              <Label htmlFor="message">메시지</Label>
              <Textarea
                id="message"
                value={formData.message}
                onChange={(e) => updateFormData('message', e.target.value)}
                placeholder="알림 메시지를 입력하세요"
                rows={3}
              />
            </div>

            <div>
              <Label htmlFor="type">알림 타입</Label>
              <Select value={formData.type} onValueChange={(value: any) => updateFormData('type', value)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="INFO">정보 (INFO)</SelectItem>
                  <SelectItem value="WARNING">경고 (WARNING)</SelectItem>
                  <SelectItem value="ERROR">오류 (ERROR)</SelectItem>
                  <SelectItem value="SUCCESS">성공 (SUCCESS)</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div>
              <Label htmlFor="actionUrl">액션 URL (선택사항)</Label>
              <Input
                id="actionUrl"
                value={formData.actionUrl}
                onChange={(e) => updateFormData('actionUrl', e.target.value)}
                placeholder="/dashboard, /inventory 등"
              />
            </div>

            <Button 
              onClick={createTestNotification} 
              disabled={loading}
              className="w-full"
            >
              {loading ? (
                <>
                  <LoadingSpinner className="mr-2 h-4 w-4" />
                  생성 중...
                </>
              ) : (
                <>
                  <Send className="mr-2 h-4 w-4" />
                  알림 생성
                </>
              )}
            </Button>
          </CardContent>
        </Card>

        {/* 빠른 테스트 알림 */}
        <Card>
          <CardHeader>
            <CardTitle>빠른 테스트 알림</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <p className="text-sm text-muted-foreground">
              미리 정의된 테스트 알림을 빠르게 생성할 수 있습니다.
            </p>

            <div className="grid grid-cols-2 gap-3">
              <Button
                variant="outline"
                onClick={() => createQuickTestNotification('INFO')}
                disabled={loading}
                className="flex items-center gap-2"
              >
                ℹ️ 정보 알림
              </Button>
              
              <Button
                variant="outline"
                onClick={() => createQuickTestNotification('WARNING')}
                disabled={loading}
                className="flex items-center gap-2"
              >
                ⚠️ 경고 알림
              </Button>
              
              <Button
                variant="outline"
                onClick={() => createQuickTestNotification('ERROR')}
                disabled={loading}
                className="flex items-center gap-2"
              >
                ❌ 오류 알림
              </Button>
              
              <Button
                variant="outline"
                onClick={() => createQuickTestNotification('SUCCESS')}
                disabled={loading}
                className="flex items-center gap-2"
              >
                ✅ 성공 알림
              </Button>
            </div>

            <div className="pt-4 border-t">
              <Button
                variant="ghost"
                onClick={() => navigate('/notifications')}
                className="w-full"
              >
                <RefreshCw className="mr-2 h-4 w-4" />
                모든 알림 보기
              </Button>
            </div>
          </CardContent>
        </Card>
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

      {/* 사용법 안내 */}
      <Card className="mt-6">
        <CardHeader>
          <CardTitle>테스트 방법</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-2 text-sm">
            <p><strong>1. 알림 생성:</strong> 위의 폼을 사용하여 테스트 알림을 생성합니다.</p>
            <p><strong>2. 실시간 확인:</strong> 알림 생성 후 헤더의 알림 아이콘을 확인해보세요.</p>
            <p><strong>3. 알림 개수:</strong> 읽지 않은 알림 개수가 실시간으로 업데이트됩니다.</p>
            <p><strong>4. 드롭다운 테스트:</strong> 알림 드롭다운을 열어 새로 생성된 알림을 확인하세요.</p>
            <p><strong>5. 페이지 이동:</strong> "모든 알림 보기" 버튼으로 전체 알림 페이지에서 확인하세요.</p>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export { NotificationTestPage }
