/**
 * 직원 카드 컴포넌트
 * 직원 정보를 카드 형태로 표시합니다
 */

import { Card, CardContent, CardHeader } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { 
  Phone, 
  Mail, 
  MapPin, 
  Calendar,
  User,
  Building,
  Users,
  Award,
  MoreHorizontal
} from 'lucide-react'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import type { Employee } from '@/types/hr'
import { KOREAN_LABELS } from '@/types/hr'
import { hrUtils } from '@/services/hrApi'

interface EmployeeCardProps {
  employee: Employee
  onEdit?: (employee: Employee) => void
  onView?: (employee: Employee) => void
  onDelete?: (employee: Employee) => void
  onTerminate?: (employee: Employee) => void
  showActions?: boolean
  className?: string
}

/**
 * 직원 카드 컴포넌트
 */
export function EmployeeCard({
  employee,
  onEdit,
  onView,
  onDelete,
  onTerminate,
  showActions = true,
  className = ''
}: EmployeeCardProps) {
  // 상태에 따른 뱃지 색상 결정
  const getStatusBadgeVariant = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'default'
      case 'ON_LEAVE':
        return 'secondary'
      case 'TERMINATED':
        return 'destructive'
      case 'SUSPENDED':
        return 'outline'
      default:
        return 'secondary'
    }
  }

  // 직원 이름의 첫 글자 추출 (아바타용)
  const getInitials = (name: string) => {
    return name.charAt(0).toUpperCase()
  }

  return (
    <Card className={`hover:shadow-md transition-shadow ${className}`}>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            {/* 프로필 이미지 또는 아바타 */}
            <Avatar className="h-12 w-12">
              <AvatarImage 
                src={employee.profileImageUrl} 
                alt={employee.name}
              />
              <AvatarFallback className="bg-primary text-primary-foreground">
                {getInitials(employee.name)}
              </AvatarFallback>
            </Avatar>
            
            <div>
              <h3 className="font-semibold text-lg">{employee.name}</h3>
              <p className="text-sm text-muted-foreground">
                {employee.employeeNumber}
              </p>
            </div>
          </div>

          {/* 액션 메뉴 */}
          {showActions && (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="sm">
                  <MoreHorizontal className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                {onView && (
                  <DropdownMenuItem onClick={() => onView(employee)}>
                    <User className="mr-2 h-4 w-4" />
                    상세보기
                  </DropdownMenuItem>
                )}
                {onEdit && (
                  <DropdownMenuItem onClick={() => onEdit(employee)}>
                    <User className="mr-2 h-4 w-4" />
                    수정
                  </DropdownMenuItem>
                )}
                {onTerminate && employee.employmentStatus === 'ACTIVE' && (
                  <DropdownMenuItem 
                    onClick={() => onTerminate(employee)}
                    className="text-orange-600"
                  >
                    <Calendar className="mr-2 h-4 w-4" />
                    퇴직처리
                  </DropdownMenuItem>
                )}
                {onDelete && (
                  <DropdownMenuItem 
                    onClick={() => onDelete(employee)}
                    className="text-destructive"
                  >
                    <User className="mr-2 h-4 w-4" />
                    삭제
                  </DropdownMenuItem>
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          )}
        </div>

        {/* 상태 뱃지 */}
        <div className="flex items-center space-x-2">
          <Badge variant={getStatusBadgeVariant(employee.employmentStatus)}>
            {KOREAN_LABELS[employee.employmentStatus]}
          </Badge>
          {employee.employmentType && (
            <Badge variant="outline">
              {KOREAN_LABELS[employee.employmentType]}
            </Badge>
          )}
        </div>
      </CardHeader>

      <CardContent className="space-y-3">
        {/* 기본 정보 */}
        <div className="grid grid-cols-1 gap-2 text-sm">
          {/* 회사 정보 */}
          <div className="flex items-center space-x-2">
            <Building className="h-4 w-4 text-muted-foreground" />
            <span>{employee.company.companyName}</span>
          </div>

          {/* 부서 정보 */}
          <div className="flex items-center space-x-2">
            <Users className="h-4 w-4 text-muted-foreground" />
            <span>{employee.department.departmentName}</span>
          </div>

          {/* 직급 정보 */}
          <div className="flex items-center space-x-2">
            <Award className="h-4 w-4 text-muted-foreground" />
            <span>{employee.position.name}</span>
          </div>

          {/* 연락처 */}
          {employee.email && (
            <div className="flex items-center space-x-2">
              <Mail className="h-4 w-4 text-muted-foreground" />
              <span className="truncate">{employee.email}</span>
            </div>
          )}

          {employee.mobile && (
            <div className="flex items-center space-x-2">
              <Phone className="h-4 w-4 text-muted-foreground" />
              <span>{employee.mobile}</span>
            </div>
          )}

          {/* 입사일 */}
          <div className="flex items-center space-x-2">
            <Calendar className="h-4 w-4 text-muted-foreground" />
            <span>
              {hrUtils.formatDate(employee.hireDate)} 
              <span className="text-muted-foreground ml-1">
                (근속 {employee.yearsOfService}년)
              </span>
            </span>
          </div>

          {/* 주소 */}
          {employee.address && (
            <div className="flex items-start space-x-2">
              <MapPin className="h-4 w-4 text-muted-foreground mt-0.5" />
              <span className="text-xs text-muted-foreground leading-relaxed">
                {employee.address}
                {employee.addressDetail && ` ${employee.addressDetail}`}
              </span>
            </div>
          )}
        </div>

        {/* 기술 스택 */}
        {employee.skills && (
          <div className="pt-2 border-t">
            <p className="text-xs font-medium text-muted-foreground mb-2">기술 스택</p>
            <div className="flex flex-wrap gap-1">
              {employee.skills.split(',').slice(0, 3).map((skill, index) => (
                <Badge key={index} variant="secondary" className="text-xs">
                  {skill.trim()}
                </Badge>
              ))}
              {employee.skills.split(',').length > 3 && (
                <Badge variant="outline" className="text-xs">
                  +{employee.skills.split(',').length - 3}
                </Badge>
              )}
            </div>
          </div>
        )}

        {/* 급여 정보 (관리자만 표시) */}
        {employee.baseSalary && (
          <div className="pt-2 border-t">
            <p className="text-xs font-medium text-muted-foreground mb-1">기본급</p>
            <p className="text-sm font-semibold">
              {hrUtils.formatCurrency(employee.baseSalary)}
            </p>
          </div>
        )}
      </CardContent>
    </Card>
  )
}

