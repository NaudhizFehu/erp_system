import { Search, X, Clock, TrendingUp, Users, Package, ShoppingCart, Building2, FolderOpen } from 'lucide-react'
import { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { customerService } from '@/services/customerService'
import { employeeService } from '@/services/employeeService'
import { productService } from '@/services/productService'
import { orderService } from '@/services/orderService'
import { departmentService } from '@/services/departmentService'
import api from '@/services/api'

/**
 * 검색 결과 타입 정의
 */
interface SearchResult {
  id: string
  title: string
  description: string
  type: 'employee' | 'product' | 'order' | 'customer' | 'department' | 'company'
  url: string
}

/**
 * 검색 제안 타입 정의
 */
interface SearchSuggestion {
  id: string
  text: string
  type: 'recent' | 'popular' | 'category'
  category?: string
  icon?: string
}

/**
 * 전역 검색 컴포넌트
 * 헤더에서 사용되는 통합 검색 기능을 제공합니다
 */
function GlobalSearch() {
  const navigate = useNavigate()
  const [open, setOpen] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const [results, setResults] = useState<SearchResult[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [recentSearches, setRecentSearches] = useState<string[]>([])
  const inputRef = useRef<HTMLInputElement>(null)

  // 최근 검색어 (실제로는 localStorage에서 가져옴)
  useEffect(() => {
    const saved = localStorage.getItem('recentSearches')
    if (saved) {
      setRecentSearches(JSON.parse(saved))
    }
  }, [])

  // 인기 검색어 및 빠른 카테고리
  const popularSearches = [
    '김철수', '노트북', '주문', '고객', '재고'
  ]

  const quickCategories: SearchSuggestion[] = [
    { id: 'cat-1', text: '직원 검색', type: 'category', category: 'employee', icon: 'Users' },
    { id: 'cat-2', text: '상품 검색', type: 'category', category: 'product', icon: 'Package' },
    { id: 'cat-3', text: '주문 검색', type: 'category', category: 'order', icon: 'ShoppingCart' },
    { id: 'cat-4', text: '고객 검색', type: 'category', category: 'customer', icon: 'Building2' },
    { id: 'cat-5', text: '부서 검색', type: 'category', category: 'department', icon: 'FolderOpen' }
  ]

  /**
   * 검색 실행
   */
  const performSearch = async (term: string) => {
    if (!term.trim()) {
      setResults([])
      return
    }

    setIsLoading(true)
    
    try {
      console.log('🔍 검색 시작:', term)
      // 실제 API 호출 (axios 인스턴스 사용으로 인증 토큰 자동 추가)
      const response = await api.get(`/search?q=${encodeURIComponent(term)}&companyId=1`)
      console.log('📡 응답 데이터:', response)
      
      // axios 응답 인터셉터에서 response.data만 반환하므로
      // response 자체가 {success: true, data: [...], message: '...'} 형태입니다
      if (response && response.success) {
        console.log('✅ 검색 성공, 결과:', response.data)
        setResults(response.data || [])
      } else {
        console.error('❌ 검색 API 오류:', response?.message || '알 수 없는 오류')
        setResults([])
      }
    } catch (error) {
      console.error('💥 검색 오류:', error)
      console.error('💥 오류 상세:', error.response?.data)
      setResults([])
    } finally {
      setIsLoading(false)
    }
  }

  /**
   * 최근 검색어 저장
   */
  const saveRecentSearch = (term: string) => {
    if (!term.trim()) return
    
    const updated = [term, ...recentSearches.filter(s => s !== term)].slice(0, 5)
    setRecentSearches(updated)
    localStorage.setItem('recentSearches', JSON.stringify(updated))
  }

  /**
   * 검색어 변경 핸들러
   */
  const handleSearchChange = (value: string) => {
    setSearchTerm(value)
    performSearch(value)
    
    // 검색어가 있으면 Popover 열기
    if (value.trim()) {
      setOpen(true)
    }
  }

  /**
   * 제안 선택 핸들러
   */
  const handleSuggestionSelect = (suggestion: SearchSuggestion) => {
    if (suggestion.type === 'category') {
      // 카테고리 선택 시 해당 카테고리로 검색
      const categoryTerm = suggestion.text.replace(' 검색', '')
      setSearchTerm(categoryTerm)
      performSearch(categoryTerm)
      saveRecentSearch(categoryTerm)
    } else {
      // 최근 검색어나 인기 검색어 선택 시
      setSearchTerm(suggestion.text)
      performSearch(suggestion.text)
      saveRecentSearch(suggestion.text)
    }
    // 검색 실행 후 결과를 보여주기 위해 Popover 열기
    setOpen(true)
  }

  /**
   * Popover 열기/닫기 핸들러
   */
  const handleOpenChange = (newOpen: boolean) => {
    setOpen(newOpen)
    if (!newOpen) {
      // Popover가 닫힐 때 검색어 초기화하지 않음 (사용자가 다시 열 수 있도록)
      // setSearchTerm('')
      // setResults([])
    }
  }

  /**
   * 검색창 클릭 핸들러
   */
  const handleInputClick = () => {
    // 검색어가 있으면 결과를 보여주고, 없으면 제안을 보여줌
    setOpen(true)
  }

  /**
   * 검색 결과 선택 핸들러
   */
  const handleSelect = (result: SearchResult) => {
    setOpen(false)
    saveRecentSearch(searchTerm)
    
    // 실제 페이지 이동
    navigate(result.url)
  }

  /**
   * 검색 초기화
   */
  const clearSearch = () => {
    setSearchTerm('')
    setResults([])
    setOpen(false)
  }

  /**
   * 타입별 아이콘 및 색상
   */
  const getTypeInfo = (type: SearchResult['type']) => {
    switch (type) {
      case 'employee':
        return { label: '직원', color: 'text-blue-600' }
      case 'product':
        return { label: '상품', color: 'text-green-600' }
      case 'order':
        return { label: '주문', color: 'text-purple-600' }
      case 'customer':
        return { label: '고객', color: 'text-orange-600' }
      case 'department':
        return { label: '부서', color: 'text-gray-600' }
      case 'company':
        return { label: '회사', color: 'text-indigo-600' }
      default:
        return { label: '기타', color: 'text-gray-600' }
    }
  }

  /**
   * 아이콘 렌더링
   */
  const renderIcon = (iconName?: string) => {
    switch (iconName) {
      case 'Users':
        return <Users className="h-4 w-4" />
      case 'Package':
        return <Package className="h-4 w-4" />
      case 'ShoppingCart':
        return <ShoppingCart className="h-4 w-4" />
      case 'Building2':
        return <Building2 className="h-4 w-4" />
      case 'FolderOpen':
        return <FolderOpen className="h-4 w-4" />
      case 'Clock':
        return <Clock className="h-4 w-4" />
      case 'TrendingUp':
        return <TrendingUp className="h-4 w-4" />
      default:
        return <Search className="h-4 w-4" />
    }
  }

  return (
    <Popover open={open} onOpenChange={handleOpenChange}>
      <PopoverTrigger asChild>
        <div className="relative max-w-sm w-full">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground pointer-events-none z-10" />
          <input
            ref={inputRef}
            type="search"
            placeholder="검색..."
            value={searchTerm}
            onChange={(e) => handleSearchChange(e.target.value)}
            onClick={handleInputClick}
            className="h-10 w-full rounded-md border border-input bg-background py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 pl-10 pr-8 search-input cursor-pointer"
          />
          {searchTerm && (
            <Button
              variant="ghost"
              size="sm"
              className="absolute right-2 top-1/2 h-6 w-6 p-0 -translate-y-1/2 z-10"
              onClick={clearSearch}
            >
              <X className="h-3 w-3" />
            </Button>
          )}
        </div>
      </PopoverTrigger>
      
      <PopoverContent 
        className="w-80 p-0" 
        align="start"
        onOpenAutoFocus={(e) => e.preventDefault()}
        onCloseAutoFocus={(e) => e.preventDefault()}
        sideOffset={4}
        avoidCollisions={true}
      >
        <div className="max-h-96 overflow-y-auto">
          <div className="p-2 border-b">
            <input
              type="text"
              placeholder="검색어를 입력하세요..."
              value={searchTerm}
              onChange={(e) => handleSearchChange(e.target.value)}
              className="w-full p-2 text-sm border-0 outline-none bg-transparent"
              autoFocus={false}
            />
          </div>
          <div>
            {isLoading ? (
              <div className="p-4 text-center text-muted-foreground">
                검색 중...
              </div>
            ) : searchTerm.trim() ? (
              // 검색어가 있을 때는 검색 결과 표시
              results.length === 0 ? (
                <div className="p-4 text-center text-muted-foreground">
                  검색 결과가 없습니다.
                </div>
              ) : (
                <div>
                  {results.map((result) => {
                    const typeInfo = getTypeInfo(result.type)
                    return (
                      <div
                        key={result.id}
                        onClick={() => handleSelect(result)}
                        className="flex items-start space-x-3 p-3 cursor-pointer hover:bg-accent transition-colors"
                      >
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center space-x-2 mb-1">
                            <h4 className="text-sm font-medium truncate">
                              {result.title}
                            </h4>
                            <span className={`text-xs px-2 py-0.5 rounded-full bg-muted ${typeInfo.color}`}>
                              {typeInfo.label}
                            </span>
                          </div>
                          <p className="text-xs text-muted-foreground line-clamp-2">
                            {result.description}
                          </p>
                          <p className="text-xs text-blue-600 mt-1">
                            클릭하여 {result.title}의 상세 정보 보기
                          </p>
                        </div>
                      </div>
                    )
                  })}
                </div>
              )
            ) : (
              // 검색어가 없을 때는 제안 목록 표시
              <div className="p-2">
                {/* 최근 검색어 */}
                {recentSearches.length > 0 && (
                  <div>
                    <div className="px-2 py-1.5 text-xs font-semibold text-muted-foreground">
                      최근 검색어
                    </div>
                    {recentSearches.map((search, index) => (
                      <div
                        key={`recent-${index}`}
                        onClick={() => handleSuggestionSelect({
                          id: `recent-${index}`,
                          text: search,
                          type: 'recent'
                        })}
                        className="flex items-center space-x-2 p-2 cursor-pointer hover:bg-accent transition-colors"
                      >
                        <Clock className="h-4 w-4 text-muted-foreground" />
                        <span className="text-sm">{search}</span>
                      </div>
                    ))}
                  </div>
                )}

                {/* 빠른 카테고리 */}
                <div>
                  <div className="px-2 py-1.5 text-xs font-semibold text-muted-foreground">
                    빠른 검색
                  </div>
                  {quickCategories.map((category) => (
                    <div
                      key={category.id}
                      onClick={() => handleSuggestionSelect(category)}
                      className="flex items-center space-x-2 p-2 cursor-pointer hover:bg-accent transition-colors"
                    >
                      {renderIcon(category.icon)}
                      <span className="text-sm">{category.text}</span>
                    </div>
                  ))}
                </div>

                {/* 인기 검색어 */}
                <div>
                  <div className="px-2 py-1.5 text-xs font-semibold text-muted-foreground">
                    인기 검색어
                  </div>
                  {popularSearches.map((search, index) => (
                    <div
                      key={`popular-${index}`}
                      onClick={() => handleSuggestionSelect({
                        id: `popular-${index}`,
                        text: search,
                        type: 'popular'
                      })}
                      className="flex items-center space-x-2 p-2 cursor-pointer hover:bg-accent transition-colors"
                    >
                      <TrendingUp className="h-4 w-4 text-muted-foreground" />
                      <span className="text-sm">{search}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </PopoverContent>
    </Popover>
  )
}

export default GlobalSearch
