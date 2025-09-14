/**
 * 활동 로그 위젯 컴포넌트
 * 최근 시스템 활동과 알림을 표시합니다
 */

import React, { useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Avatar, AvatarFallback, AvatarInitials } from '@/components/ui/avatar'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  Activity,
  Bell,
  User,
  ShoppingCart,
  Package,
  AlertTriangle,
  CheckCircle,
  Info,
  RefreshCw,
  MoreHorizontal,
  ExternalLink,
  Clock,
  Filter
} from 'lucide-react'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu'
import { formatDistanceToNow } from 'date-fns'
import { ko } from 'date-fns/locale'
import type { ActivityLog, Notification, Severity, Priority } from '@/types/dashboard'

interface ActivityWidgetProps {
  activities: ActivityLog[]
  notifications: Notification[]
  loading?: boolean
  error?: string
  className?: string
  onRefresh?: () => void
  onViewAll?: (type: 'activities' | 'notifications') => void
  onNotificationClick?: (notification: Notification) => void
  onMarkAsRead?: (notificationId: number) => void
  onMarkAllAsRead?: () => void
}

/**
 * 심각도에 따른 아이콘 반환
 */
const getSeverityIcon = (severity: Severity) => {
  switch (severity) {
    case 'ERROR':
      return <AlertTriangle className="h-4 w-4 text-red-500" />
    case 'WARNING':
      return <AlertTriangle className="h-4 w-4 text-yellow-500" />
    case 'SUCCESS':
      return <CheckCircle className="h-4 w-4 text-green-500" />
    default:
      return <Info className="h-4 w-4 text-blue-500" />
  }
}

/**
 * 우선순위에 따른 색상 반환
 */
const getPriorityColor = (priority: Priority) => {
  switch (priority) {
    case 'critical':
      return 'destructive'
    case 'high':
      return 'destructive'
    case 'medium':
      return 'secondary'
    default:
      return 'outline'
  }
}

/**
 * 모듈에 따른 아이콘 반환
 */
const getModuleIcon = (module: string) => {
  switch (module) {
    case 'sales':
      return <ShoppingCart className="h-4 w-4" />
    case 'inventory':
      return <Package className="h-4 w-4" />
    case 'hr':
      return <User className="h-4 w-4" />
    case 'accounting':
      return <Activity className="h-4 w-4" />
    default:
      return <Activity className="h-4 w-4" />
  }
}

/**
 * 활동 로그 아이템 컴포넌트
 */
const ActivityItem = ({ activity }: { activity: ActivityLog }) => {
  return (
    <div className="flex items-start space-x-3 p-3 hover:bg-gray-50 rounded-lg transition-colors">
      <div className="flex-shrink-0">
        <div className="flex items-center justify-center w-8 h-8 bg-gray-100 rounded-full">
          {getModuleIcon(activity.module)}
        </div>
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between">
          <p className="text-sm font-medium text-gray-900 truncate">
            {activity.activityDescription}
          </p>
          <div className="flex items-center space-x-2">
            {getSeverityIcon(activity.severity)}
            <span className="text-xs text-gray-500">
              {formatDistanceToNow(new Date(activity.timestamp), { 
                addSuffix: true, 
                locale: ko 
              })}
            </span>
          </div>
        </div>
        <div className="flex items-center space-x-2 mt-1">
          <Badge variant="outline" className="text-xs">
            {activity.module}
          </Badge>
          {activity.userName && (
            <span className="text-xs text-gray-500">
              by {activity.userName}
            </span>
          )}
        </div>
        {activity.details && (
          <p className="text-xs text-gray-500 mt-1 line-clamp-2">
            {activity.details}
          </p>
        )}
      </div>
    </div>
  )
}

/**
 * 알림 아이템 컴포넌트
 */
const NotificationItem = ({ 
  notification, 
  onNotificationClick, 
  onMarkAsRead 
}: { 
  notification: Notification
  onNotificationClick?: (notification: Notification) => void
  onMarkAsRead?: (notificationId: number) => void
}) => {
  const handleClick = () => {
    if (!notification.isRead && onMarkAsRead) {
      onMarkAsRead(notification.id)
    }
    if (onNotificationClick) {
      onNotificationClick(notification)
    }
  }

  return (
    <div 
      className={`flex items-start space-x-3 p-3 hover:bg-gray-50 rounded-lg transition-colors cursor-pointer ${
        !notification.isRead ? 'bg-blue-50 border-l-4 border-blue-500' : ''
      }`}
      onClick={handleClick}
    >
      <div className="flex-shrink-0">
        <div className={`flex items-center justify-center w-8 h-8 rounded-full ${
          notification.type === 'error' ? 'bg-red-100' :
          notification.type === 'warning' ? 'bg-yellow-100' :
          notification.type === 'success' ? 'bg-green-100' :
          'bg-blue-100'
        }`}>
          <Bell className={`h-4 w-4 ${
            notification.type === 'error' ? 'text-red-600' :
            notification.type === 'warning' ? 'text-yellow-600' :
            notification.type === 'success' ? 'text-green-600' :
            'text-blue-600'
          }`} />
        </div>
      </div>
      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <p className={`text-sm font-medium truncate ${
              !notification.isRead ? 'text-gray-900' : 'text-gray-600'
            }`}>
              {notification.title}
            </p>
            <p className="text-sm text-gray-500 mt-1 line-clamp-2">
              {notification.message}
            </p>
          </div>
          <div className="flex items-center space-x-2 ml-2">
            <Badge variant={getPriorityColor(notification.priority)} className="text-xs">
              {notification.priority}
            </Badge>
            {notification.actionUrl && (
              <ExternalLink className="h-3 w-3 text-gray-400" />
            )}
          </div>
        </div>
        <div className="flex items-center justify-between mt-2">
          <div className="flex items-center space-x-2">
            <Badge variant="outline" className="text-xs">
              {notification.module}
            </Badge>
            <span className="text-xs text-gray-500">
              {formatDistanceToNow(new Date(notification.createdAt), { 
                addSuffix: true, 
                locale: ko 
              })}
            </span>
          </div>
          {!notification.isRead && (
            <div className="w-2 h-2 bg-blue-500 rounded-full" />
          )}
        </div>
      </div>
    </div>
  )
}

export default function ActivityWidget({
  activities = [],
  notifications = [],
  loading = false,
  error,
  className = '',
  onRefresh,
  onViewAll,
  onNotificationClick,
  onMarkAsRead,
  onMarkAllAsRead
}: ActivityWidgetProps) {
  const [activeTab, setActiveTab] = useState('activities')

  const unreadCount = notifications.filter(n => !n.isRead).length

  if (loading) {
    return (
      <Card className={className}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center space-x-2">
              <Activity className="h-5 w-5" />
              <span>최근 활동</span>
            </CardTitle>
            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-primary"></div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[...Array(5)].map((_, index) => (
              <div key={index} className="flex items-center space-x-3">
                <div className="w-8 h-8 bg-gray-200 rounded-full animate-pulse" />
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded animate-pulse" />
                  <div className="h-3 bg-gray-200 rounded w-3/4 animate-pulse" />
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
              <Activity className="h-5 w-5" />
              <span>최근 활동</span>
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
            <Activity className="h-5 w-5" />
            <span>최근 활동</span>
            {unreadCount > 0 && (
              <Badge variant="destructive" className="text-xs">
                {unreadCount}
              </Badge>
            )}
          </CardTitle>
          <div className="flex items-center space-x-2">
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
                <DropdownMenuItem onClick={() => onViewAll?.('activities')}>
                  모든 활동 보기
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => onViewAll?.('notifications')}>
                  모든 알림 보기
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                {unreadCount > 0 && onMarkAllAsRead && (
                  <DropdownMenuItem onClick={onMarkAllAsRead}>
                    모든 알림 읽음 처리
                  </DropdownMenuItem>
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-4">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="activities" className="flex items-center space-x-2">
              <Activity className="h-4 w-4" />
              <span>활동</span>
            </TabsTrigger>
            <TabsTrigger value="notifications" className="flex items-center space-x-2">
              <Bell className="h-4 w-4" />
              <span>알림</span>
              {unreadCount > 0 && (
                <Badge variant="destructive" className="text-xs ml-1">
                  {unreadCount}
                </Badge>
              )}
            </TabsTrigger>
          </TabsList>

          <TabsContent value="activities" className="space-y-2">
            <ScrollArea className="h-80">
              {activities.length > 0 ? (
                <div className="space-y-2">
                  {activities.map((activity) => (
                    <ActivityItem key={activity.id} activity={activity} />
                  ))}
                </div>
              ) : (
                <div className="flex items-center justify-center h-32 text-gray-500">
                  <div className="text-center">
                    <Clock className="h-8 w-8 mx-auto mb-2 opacity-50" />
                    <p>최근 활동이 없습니다</p>
                  </div>
                </div>
              )}
            </ScrollArea>
            {activities.length > 0 && (
              <div className="pt-2 border-t">
                <Button 
                  variant="ghost" 
                  size="sm" 
                  className="w-full"
                  onClick={() => onViewAll?.('activities')}
                >
                  모든 활동 보기
                  <ExternalLink className="h-3 w-3 ml-2" />
                </Button>
              </div>
            )}
          </TabsContent>

          <TabsContent value="notifications" className="space-y-2">
            <ScrollArea className="h-80">
              {notifications.length > 0 ? (
                <div className="space-y-2">
                  {notifications.map((notification) => (
                    <NotificationItem 
                      key={notification.id} 
                      notification={notification}
                      onNotificationClick={onNotificationClick}
                      onMarkAsRead={onMarkAsRead}
                    />
                  ))}
                </div>
              ) : (
                <div className="flex items-center justify-center h-32 text-gray-500">
                  <div className="text-center">
                    <Bell className="h-8 w-8 mx-auto mb-2 opacity-50" />
                    <p>새로운 알림이 없습니다</p>
                  </div>
                </div>
              )}
            </ScrollArea>
            {notifications.length > 0 && (
              <div className="pt-2 border-t">
                <div className="flex space-x-2">
                  {unreadCount > 0 && onMarkAllAsRead && (
                    <Button 
                      variant="outline" 
                      size="sm" 
                      className="flex-1"
                      onClick={onMarkAllAsRead}
                    >
                      모두 읽음
                    </Button>
                  )}
                  <Button 
                    variant="ghost" 
                    size="sm" 
                    className="flex-1"
                    onClick={() => onViewAll?.('notifications')}
                  >
                    모든 알림 보기
                    <ExternalLink className="h-3 w-3 ml-2" />
                  </Button>
                </div>
              </div>
            )}
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}




