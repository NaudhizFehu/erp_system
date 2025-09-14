/**
 * 할일 목록 위젯 컴포넌트
 * 사용자의 할일과 빠른 액션을 관리합니다
 */

import React, { useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Checkbox } from '@/components/ui/checkbox'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  CheckSquare,
  Plus,
  MoreHorizontal,
  Clock,
  AlertTriangle,
  CheckCircle,
  Circle,
  Calendar,
  User,
  ExternalLink,
  RefreshCw,
  Zap,
  ArrowRight
} from 'lucide-react'
import { formatDistanceToNow, format } from 'date-fns'
import { ko } from 'date-fns/locale'
import type { TodoItem, TodoCreateRequest, QuickAction, Priority, TodoStatus } from '@/types/dashboard'

interface TodoWidgetProps {
  todos: TodoItem[]
  quickActions: QuickAction[]
  loading?: boolean
  error?: string
  className?: string
  onRefresh?: () => void
  onCreateTodo?: (todo: TodoCreateRequest) => void
  onUpdateTodoStatus?: (todoId: number, status: TodoStatus) => void
  onQuickActionClick?: (action: QuickAction) => void
  onViewAll?: () => void
}

/**
 * 우선순위에 따른 색상과 아이콘 반환
 */
const getPriorityConfig = (priority: Priority) => {
  switch (priority) {
    case 'critical':
      return { color: 'destructive', icon: AlertTriangle, text: '긴급' }
    case 'high':
      return { color: 'destructive', icon: AlertTriangle, text: '높음' }
    case 'medium':
      return { color: 'secondary', icon: Clock, text: '보통' }
    default:
      return { color: 'outline', icon: Circle, text: '낮음' }
  }
}

/**
 * 상태에 따른 아이콘 반환
 */
const getStatusIcon = (status: TodoStatus) => {
  switch (status) {
    case 'completed':
      return <CheckCircle className="h-4 w-4 text-green-500" />
    case 'in_progress':
      return <Clock className="h-4 w-4 text-blue-500" />
    case 'cancelled':
      return <Circle className="h-4 w-4 text-gray-400" />
    default:
      return <Circle className="h-4 w-4 text-gray-500" />
  }
}

/**
 * 할일 아이템 컴포넌트
 */
const TodoItemComponent = ({ 
  todo, 
  onUpdateStatus 
}: { 
  todo: TodoItem
  onUpdateStatus?: (todoId: number, status: TodoStatus) => void
}) => {
  const priorityConfig = getPriorityConfig(todo.priority)
  const PriorityIcon = priorityConfig.icon
  
  const isOverdue = todo.dueDate && new Date(todo.dueDate) < new Date() && todo.status !== 'completed'
  
  const handleStatusChange = (checked: boolean) => {
    if (onUpdateStatus) {
      onUpdateStatus(todo.id, checked ? 'completed' : 'pending')
    }
  }

  return (
    <div className={`flex items-start space-x-3 p-3 rounded-lg transition-colors hover:bg-gray-50 ${
      isOverdue ? 'bg-red-50 border-l-4 border-red-500' : ''
    }`}>
      <div className="flex-shrink-0 pt-1">
        <Checkbox
          checked={todo.status === 'completed'}
          onCheckedChange={handleStatusChange}
          disabled={todo.status === 'cancelled'}
        />
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <p className={`text-sm font-medium ${
              todo.status === 'completed' ? 'line-through text-gray-500' : 'text-gray-900'
            }`}>
              {todo.title}
            </p>
            {todo.description && (
              <p className="text-xs text-gray-500 mt-1 line-clamp-2">
                {todo.description}
              </p>
            )}
          </div>
          <div className="flex items-center space-x-2 ml-2">
            <Badge variant={priorityConfig.color as any} className="text-xs">
              <PriorityIcon className="h-3 w-3 mr-1" />
              {priorityConfig.text}
            </Badge>
            {getStatusIcon(todo.status)}
          </div>
        </div>
        
        <div className="flex items-center justify-between mt-2">
          <div className="flex items-center space-x-2 text-xs text-gray-500">
            <Badge variant="outline" className="text-xs">
              {todo.module}
            </Badge>
            {todo.assignedTo && (
              <div className="flex items-center space-x-1">
                <User className="h-3 w-3" />
                <span>{todo.assignedTo}</span>
              </div>
            )}
            {todo.dueDate && (
              <div className={`flex items-center space-x-1 ${isOverdue ? 'text-red-600 font-medium' : ''}`}>
                <Calendar className="h-3 w-3" />
                <span>
                  {format(new Date(todo.dueDate), 'MM/dd', { locale: ko })}
                  {isOverdue && ' (지연)'}
                </span>
              </div>
            )}
          </div>
          <span className="text-xs text-gray-400">
            {formatDistanceToNow(new Date(todo.createdAt), { addSuffix: true, locale: ko })}
          </span>
        </div>
      </div>
    </div>
  )
}

/**
 * 빠른 액션 아이템 컴포넌트
 */
const QuickActionItem = ({ 
  action, 
  onClick 
}: { 
  action: QuickAction
  onClick?: (action: QuickAction) => void
}) => {
  return (
    <Button
      variant="ghost"
      className="w-full justify-start h-auto p-3 hover:bg-gray-50"
      onClick={() => onClick?.(action)}
    >
      <div className="flex items-center space-x-3 w-full">
        <div className="flex-shrink-0">
          <div className="w-8 h-8 bg-primary/10 rounded-lg flex items-center justify-center">
            <Zap className="h-4 w-4 text-primary" />
          </div>
        </div>
        <div className="flex-1 text-left">
          <p className="text-sm font-medium text-gray-900">{action.title}</p>
          <p className="text-xs text-gray-500">{action.description}</p>
        </div>
        <ArrowRight className="h-4 w-4 text-gray-400" />
      </div>
    </Button>
  )
}

/**
 * 새 할일 생성 다이얼로그
 */
const CreateTodoDialog = ({ 
  onCreateTodo 
}: { 
  onCreateTodo?: (todo: TodoCreateRequest) => void
}) => {
  const [open, setOpen] = useState(false)
  const [formData, setFormData] = useState<TodoCreateRequest>({
    title: '',
    description: '',
    priority: 'medium',
    dueDate: '',
    module: 'system'
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (formData.title.trim()) {
      onCreateTodo?.(formData)
      setFormData({
        title: '',
        description: '',
        priority: 'medium',
        dueDate: '',
        module: 'system'
      })
      setOpen(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button size="sm" className="flex items-center space-x-2">
          <Plus className="h-4 w-4" />
          <span>새 할일</span>
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>새 할일 추가</DialogTitle>
          <DialogDescription>
            새로운 할일을 추가합니다.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Input
              placeholder="할일 제목"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              required
            />
          </div>
          <div>
            <Textarea
              placeholder="상세 설명 (선택사항)"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              rows={3}
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <Select 
                value={formData.priority} 
                onValueChange={(value: Priority) => setFormData({ ...formData, priority: value })}
              >
                <SelectTrigger>
                  <SelectValue placeholder="우선순위" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="low">낮음</SelectItem>
                  <SelectItem value="medium">보통</SelectItem>
                  <SelectItem value="high">높음</SelectItem>
                  <SelectItem value="critical">긴급</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <Input
                type="date"
                value={formData.dueDate}
                onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
              />
            </div>
          </div>
          <div>
            <Select 
              value={formData.module} 
              onValueChange={(value) => setFormData({ ...formData, module: value })}
            >
              <SelectTrigger>
                <SelectValue placeholder="모듈" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="sales">영업관리</SelectItem>
                <SelectItem value="inventory">재고관리</SelectItem>
                <SelectItem value="hr">인사관리</SelectItem>
                <SelectItem value="accounting">회계관리</SelectItem>
                <SelectItem value="system">시스템</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>
              취소
            </Button>
            <Button type="submit">
              추가
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}

export default function TodoWidget({
  todos = [],
  quickActions = [],
  loading = false,
  error,
  className = '',
  onRefresh,
  onCreateTodo,
  onUpdateTodoStatus,
  onQuickActionClick,
  onViewAll
}: TodoWidgetProps) {
  const [activeTab, setActiveTab] = useState('todos')

  const pendingTodos = todos.filter(todo => todo.status === 'pending' || todo.status === 'in_progress')
  const completedTodos = todos.filter(todo => todo.status === 'completed')
  const overdueTodos = todos.filter(todo => 
    todo.dueDate && 
    new Date(todo.dueDate) < new Date() && 
    todo.status !== 'completed'
  )

  if (loading) {
    return (
      <Card className={className}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center space-x-2">
              <CheckSquare className="h-5 w-5" />
              <span>할일 & 빠른 액션</span>
            </CardTitle>
            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-primary"></div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[...Array(4)].map((_, index) => (
              <div key={index} className="flex items-center space-x-3">
                <div className="w-4 h-4 bg-gray-200 rounded animate-pulse" />
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded animate-pulse" />
                  <div className="h-3 bg-gray-200 rounded w-2/3 animate-pulse" />
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    )
  }

  if (error) {
    return (
      <Card className={className}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center space-x-2">
              <CheckSquare className="h-5 w-5" />
              <span>할일 & 빠른 액션</span>
            </CardTitle>
            <Button variant="ghost" size="sm" onClick={onRefresh}>
              <RefreshCw className="h-4 w-4" />
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8">
            <p className="text-red-500 mb-4">데이터 로딩 중 오류가 발생했습니다</p>
            <Button variant="outline" onClick={onRefresh}>
              <RefreshCw className="h-4 w-4 mr-2" />
              다시 시도
            </Button>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center space-x-2">
            <CheckSquare className="h-5 w-5" />
            <span>할일 & 빠른 액션</span>
            {overdueTodos.length > 0 && (
              <Badge variant="destructive" className="text-xs">
                {overdueTodos.length} 지연
              </Badge>
            )}
          </CardTitle>
          <div className="flex items-center space-x-2">
            {onCreateTodo && (
              <CreateTodoDialog onCreateTodo={onCreateTodo} />
            )}
            {onRefresh && (
              <Button variant="ghost" size="sm" onClick={onRefresh}>
                <RefreshCw className="h-4 w-4" />
              </Button>
            )}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="sm">
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={onViewAll}>
                  모든 할일 보기
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem>
                  완료된 할일 숨기기
                </DropdownMenuItem>
                <DropdownMenuItem>
                  우선순위 정렬
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-4">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="todos" className="flex items-center space-x-2">
              <CheckSquare className="h-4 w-4" />
              <span>할일</span>
              {pendingTodos.length > 0 && (
                <Badge variant="secondary" className="text-xs ml-1">
                  {pendingTodos.length}
                </Badge>
              )}
            </TabsTrigger>
            <TabsTrigger value="actions" className="flex items-center space-x-2">
              <Zap className="h-4 w-4" />
              <span>빠른 액션</span>
            </TabsTrigger>
          </TabsList>

          <TabsContent value="todos" className="space-y-2">
            {/* 진행 상황 요약 */}
            <div className="grid grid-cols-3 gap-4 p-3 bg-gray-50 rounded-lg">
              <div className="text-center">
                <p className="text-lg font-semibold text-gray-900">{pendingTodos.length}</p>
                <p className="text-xs text-gray-500">진행중</p>
              </div>
              <div className="text-center">
                <p className="text-lg font-semibold text-green-600">{completedTodos.length}</p>
                <p className="text-xs text-gray-500">완료</p>
              </div>
              <div className="text-center">
                <p className="text-lg font-semibold text-red-600">{overdueTodos.length}</p>
                <p className="text-xs text-gray-500">지연</p>
              </div>
            </div>

            <ScrollArea className="h-72">
              {todos.length > 0 ? (
                <div className="space-y-2">
                  {/* 지연된 할일 먼저 표시 */}
                  {overdueTodos.map((todo) => (
                    <TodoItemComponent 
                      key={`overdue-${todo.id}`} 
                      todo={todo} 
                      onUpdateStatus={onUpdateTodoStatus}
                    />
                  ))}
                  {/* 나머지 할일 */}
                  {todos
                    .filter(todo => !overdueTodos.includes(todo))
                    .sort((a, b) => {
                      const priorityOrder = { critical: 4, high: 3, medium: 2, low: 1 }
                      return priorityOrder[b.priority] - priorityOrder[a.priority]
                    })
                    .map((todo) => (
                      <TodoItemComponent 
                        key={todo.id} 
                        todo={todo} 
                        onUpdateStatus={onUpdateTodoStatus}
                      />
                    ))}
                </div>
              ) : (
                <div className="flex items-center justify-center h-32 text-gray-500">
                  <div className="text-center">
                    <CheckSquare className="h-8 w-8 mx-auto mb-2 opacity-50" />
                    <p>할일이 없습니다</p>
                  </div>
                </div>
              )}
            </ScrollArea>
          </TabsContent>

          <TabsContent value="actions" className="space-y-2">
            <ScrollArea className="h-80">
              {quickActions.length > 0 ? (
                <div className="space-y-2">
                  {quickActions
                    .filter(action => action.isEnabled)
                    .sort((a, b) => a.sortOrder - b.sortOrder)
                    .map((action) => (
                      <QuickActionItem 
                        key={action.id} 
                        action={action} 
                        onClick={onQuickActionClick}
                      />
                    ))}
                </div>
              ) : (
                <div className="flex items-center justify-center h-32 text-gray-500">
                  <div className="text-center">
                    <Zap className="h-8 w-8 mx-auto mb-2 opacity-50" />
                    <p>사용 가능한 빠른 액션이 없습니다</p>
                  </div>
                </div>
              )}
            </ScrollArea>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}




