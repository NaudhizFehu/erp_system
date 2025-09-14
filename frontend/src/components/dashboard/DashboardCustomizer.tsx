/**
 * 대시보드 커스터마이저 컴포넌트
 * 사용자가 대시보드 레이아웃과 위젯을 커스터마이징할 수 있습니다
 */

import React, { useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Switch } from '@/components/ui/switch'
import { Slider } from '@/components/ui/slider'
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
} from '@/components/ui/dialog'
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '@/components/ui/tabs'
import { ScrollArea } from '@/components/ui/scroll-area'
import {
  DragDropContext,
  Droppable,
  Draggable,
  DropResult
} from '@hello-pangea/dnd'
import {
  Settings,
  Eye,
  EyeOff,
  Move,
  Palette,
  Layout,
  Grid3X3,
  Maximize2,
  Minimize2,
  RotateCcw,
  Save,
  X,
  Plus,
  Trash2
} from 'lucide-react'
import toast from 'react-hot-toast'
import type { 
  UserDashboardConfig, 
  WidgetConfig, 
  Theme, 
  Layout as LayoutType,
  ChartType,
  WidgetType
} from '@/types/dashboard'

interface DashboardCustomizerProps {
  config: UserDashboardConfig
  open: boolean
  onOpenChange: (open: boolean) => void
  onSave: (config: UserDashboardConfig) => void
  onReset?: () => void
  userRole: string
}

/**
 * 위젯 설정 카드 컴포넌트
 */
const WidgetConfigCard = ({
  widget,
  onUpdate,
  onRemove,
  userRole
}: {
  widget: WidgetConfig
  onUpdate: (id: string, updates: Partial<WidgetConfig>) => void
  onRemove: (id: string) => void
  userRole: string
}) => {
  const getWidgetIcon = (type: WidgetType) => {
    switch (type) {
      case 'chart': return '📊'
      case 'summary': return '📋'
      case 'list': return '📝'
      case 'table': return '🗂️'
      case 'metric': return '🔢'
      default: return '⚙️'
    }
  }

  const availableChartTypes: { value: ChartType; label: string }[] = [
    { value: 'line', label: '선형 차트' },
    { value: 'bar', label: '막대 차트' },
    { value: 'pie', label: '원형 차트' },
    { value: 'doughnut', label: '도넛 차트' },
    { value: 'area', label: '영역 차트' },
    { value: 'scatter', label: '산점도' }
  ]

  return (
    <Draggable draggableId={widget.id} index={0}>
      {(provided, snapshot) => (
        <Card
          ref={provided.innerRef}
          {...provided.draggableProps}
          className={`transition-all ${snapshot.isDragging ? 'rotate-2 shadow-lg' : ''}`}
        >
          <CardHeader className="pb-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <div {...provided.dragHandleProps} className="cursor-grab">
                  <Move className="h-4 w-4 text-gray-400" />
                </div>
                <span className="text-lg">{getWidgetIcon(widget.type)}</span>
                <CardTitle className="text-base">{widget.title}</CardTitle>
              </div>
              <div className="flex items-center space-x-2">
                <Switch
                  checked={widget.isVisible}
                  onCheckedChange={(checked) => onUpdate(widget.id, { isVisible: checked })}
                />
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => onRemove(widget.id)}
                  className="text-red-500 hover:text-red-700"
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* 위젯 크기 설정 */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="text-sm font-medium">너비</label>
                <Slider
                  value={[widget.width]}
                  onValueChange={([value]) => onUpdate(widget.id, { width: value })}
                  max={12}
                  min={1}
                  step={1}
                  className="mt-2"
                />
                <span className="text-xs text-gray-500">{widget.width}/12</span>
              </div>
              <div>
                <label className="text-sm font-medium">높이</label>
                <Slider
                  value={[widget.height]}
                  onValueChange={([value]) => onUpdate(widget.id, { height: value })}
                  max={12}
                  min={2}
                  step={1}
                  className="mt-2"
                />
                <span className="text-xs text-gray-500">{widget.height} 단위</span>
              </div>
            </div>

            {/* 차트 타입 설정 (차트 위젯인 경우) */}
            {widget.type === 'chart' && (
              <div>
                <label className="text-sm font-medium">차트 타입</label>
                <Select
                  value={widget.chartType || 'line'}
                  onValueChange={(value: ChartType) => onUpdate(widget.id, { chartType: value })}
                >
                  <SelectTrigger className="mt-2">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {availableChartTypes.map((type) => (
                      <SelectItem key={type.value} value={type.value}>
                        {type.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            )}

            {/* 시간 범위 설정 */}
            <div>
              <label className="text-sm font-medium">시간 범위</label>
              <Select
                value={widget.timeRange || 'monthly'}
                onValueChange={(value) => onUpdate(widget.id, { timeRange: value })}
              >
                <SelectTrigger className="mt-2">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="daily">일별</SelectItem>
                  <SelectItem value="weekly">주별</SelectItem>
                  <SelectItem value="monthly">월별</SelectItem>
                  <SelectItem value="yearly">연별</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* 위젯 정보 */}
            <div className="pt-2 border-t">
              <div className="flex items-center justify-between text-xs text-gray-500">
                <span>데이터 소스: {widget.dataSource}</span>
                <Badge variant="outline">{widget.type}</Badge>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </Draggable>
  )
}

/**
 * 사용 가능한 위젯 목록
 */
const getAvailableWidgets = (userRole: string): Partial<WidgetConfig>[] => {
  const baseWidgets = [
    {
      id: 'overview',
      title: '전체 현황',
      type: 'summary' as WidgetType,
      dataSource: 'overview',
      width: 12,
      height: 4
    },
    {
      id: 'revenue-chart',
      title: '매출 차트',
      type: 'chart' as WidgetType,
      dataSource: 'revenue',
      chartType: 'line' as ChartType,
      width: 8,
      height: 6
    },
    {
      id: 'activities',
      title: '최근 활동',
      type: 'list' as WidgetType,
      dataSource: 'activities',
      width: 4,
      height: 6
    },
    {
      id: 'todos',
      title: '할일 목록',
      type: 'list' as WidgetType,
      dataSource: 'todos',
      width: 4,
      height: 6
    }
  ]

  // 관리자/매니저 전용 위젯
  if (userRole === 'ADMIN' || userRole === 'MANAGER') {
    baseWidgets.push(
      {
        id: 'hr-chart',
        title: '인사 현황',
        type: 'chart' as WidgetType,
        dataSource: 'hr',
        chartType: 'bar' as ChartType,
        width: 6,
        height: 6
      },
      {
        id: 'inventory-chart',
        title: '재고 현황',
        type: 'chart' as WidgetType,
        dataSource: 'inventory',
        chartType: 'pie' as ChartType,
        width: 6,
        height: 6
      }
    )
  }

  // 관리자 전용 위젯
  if (userRole === 'ADMIN') {
    baseWidgets.push(
      {
        id: 'system-status',
        title: '시스템 상태',
        type: 'metric' as WidgetType,
        dataSource: 'system',
        width: 12,
        height: 3
      }
    )
  }

  return baseWidgets
}

export default function DashboardCustomizer({
  config,
  open,
  onOpenChange,
  onSave,
  onReset,
  userRole
}: DashboardCustomizerProps) {
  const [localConfig, setLocalConfig] = useState<UserDashboardConfig>(config)
  const [activeTab, setActiveTab] = useState('widgets')

  // 위젯 업데이트
  const updateWidget = (widgetId: string, updates: Partial<WidgetConfig>) => {
    setLocalConfig(prev => ({
      ...prev,
      widgets: prev.widgets.map(w => 
        w.id === widgetId ? { ...w, ...updates } : w
      )
    }))
  }

  // 위젯 제거
  const removeWidget = (widgetId: string) => {
    setLocalConfig(prev => ({
      ...prev,
      widgets: prev.widgets.filter(w => w.id !== widgetId)
    }))
  }

  // 위젯 추가
  const addWidget = (widget: Partial<WidgetConfig>) => {
    const newWidget: WidgetConfig = {
      id: widget.id || `widget-${Date.now()}`,
      title: widget.title || '새 위젯',
      type: widget.type || 'summary',
      width: widget.width || 6,
      height: widget.height || 4,
      positionX: 0,
      positionY: 0,
      isVisible: true,
      dataSource: widget.dataSource || 'custom',
      chartType: widget.chartType,
      timeRange: widget.timeRange || 'monthly',
      settings: widget.settings || {}
    }

    setLocalConfig(prev => ({
      ...prev,
      widgets: [...prev.widgets, newWidget]
    }))
  }

  // 드래그 앤 드롭 처리
  const handleDragEnd = (result: DropResult) => {
    if (!result.destination) return

    const widgets = Array.from(localConfig.widgets)
    const [reorderedWidget] = widgets.splice(result.source.index, 1)
    widgets.splice(result.destination.index, 0, reorderedWidget)

    setLocalConfig(prev => ({
      ...prev,
      widgets
    }))
  }

  // 테마 변경
  const updateTheme = (theme: Theme) => {
    setLocalConfig(prev => ({ ...prev, theme }))
  }

  // 레이아웃 변경
  const updateLayout = (layout: LayoutType) => {
    setLocalConfig(prev => ({ ...prev, layout }))
  }

  // 설정 저장
  const handleSave = () => {
    onSave({
      ...localConfig,
      lastUpdated: new Date().toISOString()
    })
    onOpenChange(false)
    toast.success('대시보드 설정이 저장되었습니다')
  }

  // 설정 초기화
  const handleReset = () => {
    if (onReset) {
      onReset()
      setLocalConfig(config)
      toast.info('설정이 초기화되었습니다')
    }
  }

  const availableWidgets = getAvailableWidgets(userRole)
  const currentWidgetIds = localConfig.widgets.map(w => w.id)
  const availableToAdd = availableWidgets.filter(w => !currentWidgetIds.includes(w.id!))

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[80vh] overflow-hidden">
        <DialogHeader>
          <DialogTitle className="flex items-center space-x-2">
            <Settings className="h-5 w-5" />
            <span>대시보드 커스터마이징</span>
          </DialogTitle>
          <DialogDescription>
            대시보드 레이아웃과 위젯을 사용자 맞춤 설정할 수 있습니다.
          </DialogDescription>
        </DialogHeader>

        <Tabs value={activeTab} onValueChange={setActiveTab} className="flex-1">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="widgets">위젯 관리</TabsTrigger>
            <TabsTrigger value="layout">레이아웃</TabsTrigger>
            <TabsTrigger value="theme">테마</TabsTrigger>
          </TabsList>

          <TabsContent value="widgets" className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-medium">위젯 설정</h3>
              <div className="flex items-center space-x-2">
                <span className="text-sm text-gray-500">
                  {localConfig.widgets.filter(w => w.isVisible).length}개 위젯 표시 중
                </span>
              </div>
            </div>

            {/* 새 위젯 추가 */}
            {availableToAdd.length > 0 && (
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">위젯 추가</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                    {availableToAdd.map((widget) => (
                      <Button
                        key={widget.id}
                        variant="outline"
                        className="h-auto p-3 flex flex-col items-center space-y-2"
                        onClick={() => addWidget(widget)}
                      >
                        <Plus className="h-4 w-4" />
                        <span className="text-sm">{widget.title}</span>
                      </Button>
                    ))}
                  </div>
                </CardContent>
              </Card>
            )}

            {/* 현재 위젯 목록 */}
            <ScrollArea className="h-96">
              <DragDropContext onDragEnd={handleDragEnd}>
                <Droppable droppableId="widgets">
                  {(provided) => (
                    <div
                      {...provided.droppableProps}
                      ref={provided.innerRef}
                      className="space-y-4"
                    >
                      {localConfig.widgets.map((widget, index) => (
                        <Draggable key={widget.id} draggableId={widget.id} index={index}>
                          {(provided, snapshot) => (
                            <div
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                            >
                              <WidgetConfigCard
                                widget={widget}
                                onUpdate={updateWidget}
                                onRemove={removeWidget}
                                userRole={userRole}
                              />
                            </div>
                          )}
                        </Draggable>
                      ))}
                      {provided.placeholder}
                    </div>
                  )}
                </Droppable>
              </DragDropContext>
            </ScrollArea>
          </TabsContent>

          <TabsContent value="layout" className="space-y-4">
            <div>
              <h3 className="text-lg font-medium mb-4">레이아웃 설정</h3>
              
              <div className="space-y-4">
                <div>
                  <label className="text-sm font-medium">레이아웃 타입</label>
                  <div className="grid grid-cols-3 gap-3 mt-2">
                    {[
                      { value: 'grid', label: '그리드', icon: Grid3X3 },
                      { value: 'masonry', label: '벽돌식', icon: Layout },
                      { value: 'custom', label: '사용자정의', icon: Maximize2 }
                    ].map(({ value, label, icon: Icon }) => (
                      <Button
                        key={value}
                        variant={localConfig.layout === value ? 'default' : 'outline'}
                        className="h-20 flex flex-col items-center space-y-2"
                        onClick={() => updateLayout(value as LayoutType)}
                      >
                        <Icon className="h-6 w-6" />
                        <span className="text-sm">{label}</span>
                      </Button>
                    ))}
                  </div>
                </div>

                <div className="pt-4 border-t">
                  <h4 className="font-medium mb-2">레이아웃 미리보기</h4>
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <div className="grid grid-cols-12 gap-2 h-32">
                      {localConfig.widgets
                        .filter(w => w.isVisible)
                        .slice(0, 6)
                        .map((widget, index) => (
                          <div
                            key={widget.id}
                            className="bg-white rounded border flex items-center justify-center text-xs font-medium"
                            style={{
                              gridColumn: `span ${Math.min(widget.width, 12)}`,
                              gridRow: `span 1`
                            }}
                          >
                            {widget.title}
                          </div>
                        ))}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </TabsContent>

          <TabsContent value="theme" className="space-y-4">
            <div>
              <h3 className="text-lg font-medium mb-4">테마 설정</h3>
              
              <div className="space-y-4">
                <div>
                  <label className="text-sm font-medium">색상 테마</label>
                  <div className="grid grid-cols-3 gap-3 mt-2">
                    {[
                      { value: 'light', label: '라이트', color: 'bg-white border' },
                      { value: 'dark', label: '다크', color: 'bg-gray-900' },
                      { value: 'auto', label: '자동', color: 'bg-gradient-to-r from-white to-gray-900' }
                    ].map(({ value, label, color }) => (
                      <Button
                        key={value}
                        variant={localConfig.theme === value ? 'default' : 'outline'}
                        className="h-20 flex flex-col items-center space-y-2"
                        onClick={() => updateTheme(value as Theme)}
                      >
                        <div className={`w-8 h-8 rounded ${color}`} />
                        <span className="text-sm">{label}</span>
                      </Button>
                    ))}
                  </div>
                </div>

                <div className="pt-4 border-t">
                  <h4 className="font-medium mb-2">테마 미리보기</h4>
                  <div className={`p-4 rounded-lg border ${
                    localConfig.theme === 'dark' ? 'bg-gray-900 text-white' : 'bg-white'
                  }`}>
                    <div className="flex items-center justify-between mb-2">
                      <h5 className="font-medium">샘플 위젯</h5>
                      <Badge>테스트</Badge>
                    </div>
                    <p className="text-sm opacity-75">
                      선택한 테마로 대시보드가 표시됩니다.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </TabsContent>
        </Tabs>

        <DialogFooter className="flex items-center justify-between">
          <div className="flex space-x-2">
            {onReset && (
              <Button variant="outline" onClick={handleReset}>
                <RotateCcw className="h-4 w-4 mr-2" />
                초기화
              </Button>
            )}
          </div>
          <div className="flex space-x-2">
            <Button variant="outline" onClick={() => onOpenChange(false)}>
              <X className="h-4 w-4 mr-2" />
              취소
            </Button>
            <Button onClick={handleSave}>
              <Save className="h-4 w-4 mr-2" />
              저장
            </Button>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}

